package com.brocamp.babystore.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PaymentMethods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentMethods_id")
    private long id;
    private String paymentMode;
}
