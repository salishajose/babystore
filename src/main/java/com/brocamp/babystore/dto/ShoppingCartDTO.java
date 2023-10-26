package com.brocamp.babystore.dto;

import com.brocamp.babystore.model.Product;
import com.brocamp.babystore.model.Users;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingCartDTO {
    private long id;
    private Users users;
    private Product product;
    private long quantity;
    private double individualRate;
    private double totalRate;
}
