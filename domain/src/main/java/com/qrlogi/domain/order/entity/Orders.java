package com.qrlogi.domain.order.entity;


import com.qrlogi.domain.buyer.entity.Buyer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Orders {

    @Id
    @Column(length = 36)
    private String id; // UUID

    @Column(name = "order_number", nullable = false, unique = true)
    private Long orderNumber; ///Snowflake적용한 내부 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;


    public void cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
    }
}