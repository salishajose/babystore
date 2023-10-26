package com.brocamp.babystore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
@Data
@Entity
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String couponCode;
    private String description;
    private double discount;
    //per user apply coupon count
    private long count;
    private double maximumAmount;
    private double minimumOrderAmount;
    private LocalDate expiryDate;
    private Date createdAt;
    private Date updateOn;
    private boolean activated;
    private boolean deleted;

}
