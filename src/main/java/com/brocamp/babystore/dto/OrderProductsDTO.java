package com.brocamp.babystore.dto;

import com.brocamp.babystore.model.OrderDetails;
import com.brocamp.babystore.model.Product;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class OrderProductsDTO {
    private long id;
    private OrderDetails orderDetails;
    private Product product;
    private long quantity;
    private double individualRate;
    private double totalRate;
}
