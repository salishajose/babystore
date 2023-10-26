package com.brocamp.babystore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
@Data
public class CouponDTO {
    private long id;

    @NotNull(message = "is required")
    @Size(min=5, max=30,message = "Please enter coupon code")
    @Pattern(regexp="^[A-Za-z0-9 ]+$",message = "No special characters allowed")
    private String couponCode;


    @NotNull(message = "is required")
    @Size(min=5, max=30,message = "Please enter coupon code")
    @Pattern(regexp="^[A-Za-z0-9 ]+$",message = "No special characters allowed")
    private String description;


    @NotNull(message = "is required")
    private double discount;
    //per user apply coupon count

    @NotNull(message = "is required")
    private long count;


    @NotNull(message = "is required")
    private double maximumAmount;


    @NotNull(message = "is required")
    private double minimumOrderAmount;
    private Date createdAt;
    private Date updateOn;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private boolean activated;
    private boolean deleted;
}
