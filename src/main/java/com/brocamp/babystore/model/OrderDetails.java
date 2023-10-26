package com.brocamp.babystore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
@Entity
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="orderDetails_id")
    private long id;
    private String orderStatus;
    private Date orderDate;
    private Date deliveryDate;
    private double orderAmount;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="users_id",referencedColumnName = "users_id")
    private Users users;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="address_id",referencedColumnName = "address_id")
    private Address address;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="paymentMethods_id",referencedColumnName = "paymentMethods_id")
    private PaymentMethods paymentMethods;
    @Column(nullable = true)
    private long couponId;
    @Column(nullable = true)
    private double discount;
    @Column(nullable = true)
    private double finalAmount;

}
