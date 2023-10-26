package com.brocamp.babystore.dto;

import com.brocamp.babystore.model.Address;
import com.brocamp.babystore.model.OrderProducts;
import com.brocamp.babystore.model.PaymentMethods;
import com.brocamp.babystore.model.Users;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class OrderDetailsDTO {
    private long id;
    private String orderStatus;
    private Date orderDate;
    private Date deliveryDate;
    private double orderAmount;
    private Users users;
    private long addressId;
    private long paymentMethodsId;
    private Address address;
    private PaymentMethods paymentMethods;
    private List<OrderProducts> orderProducts;
    private long couponId;
    private double discount;
    private double finalAmount;
}
