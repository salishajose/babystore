package com.brocamp.babystore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class OrderPayments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    @ManyToOne
    @JoinColumn(name="orderDetails_id")
    private OrderDetails orderDetails;
    private String orderId;
    private String paymentId;
    private String receipt;
    private String status;
    private Date createdAt;
    private Date updateOn;


}
