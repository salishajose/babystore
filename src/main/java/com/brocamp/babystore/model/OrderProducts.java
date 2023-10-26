package com.brocamp.babystore.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OrderProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderProducts_id")
    private long id;
    @ManyToOne
    @JoinColumn(name="orderDetails_id")
    private OrderDetails orderDetails;
    @ManyToOne
    @JoinColumn(name="product_id",referencedColumnName = "product_id")
    private Product product;
    private long quantity;
    private double individualRate;
    private double totalRate;
}
