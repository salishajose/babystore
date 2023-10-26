package com.brocamp.babystore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shoppingCart_id")
    private long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="users_id",referencedColumnName = "users_id")
    private Users users;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="product_id",referencedColumnName = "product_id")
    private Product product;
    private long quantity;
    private double individualRate;
    private double totalRate;
    private boolean deleted;
}
