package com.qrlogi.domain.orderitem.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class QrService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;


    //serialId로 qr생성
    //img로 변환
    //s3저장
    //url을 반환(접속용)
    public String createQrUrl(String serialId) {

        try {
            BufferedImage qrImg = createQrImg(serialId); //qr 생성

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImg, "png", outputStream);
            byte[] imgBytes = outputStream.toByteArray();


            String fileName = "qr/" + serialId + ".png";
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imgBytes.length);
            metadata.setContentType("image/png");

            amazonS3.putObject(
                    new PutObjectRequest(bucketName, fileName, new ByteArrayInputStream(imgBytes), metadata)
            );

            return amazonS3.getUrl(bucketName, fileName).toString();



        } catch (IOException | WriterException e) {
            throw new RuntimeException("QR 이미지 생성 실패", e);
        }


    }

    private BufferedImage createQrImg(String serialId) throws WriterException {
        BitMatrix matrix = new QRCodeWriter().encode(serialId, BarcodeFormat.QR_CODE, 150, 150);
        return MatrixToImageWriter.toBufferedImage(matrix);

    }


}
