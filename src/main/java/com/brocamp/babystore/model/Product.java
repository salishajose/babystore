package com.brocamp.babystore.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name="product", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private Long id;
    private String name;
    private String description;
    private double costPrice;
    private double salePrice;
    private long currentQuantity;

    @ElementCollection
    @CollectionTable(name = "product_image_urls", joinColumns = @JoinColumn(name = "product_id"))
    private List<String> imageUrls;
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="category_id",referencedColumnName = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="brand_id",referencedColumnName = "brand_id")
    private Brand brand;

    private boolean productDeleted;
    private boolean productActivated;



}