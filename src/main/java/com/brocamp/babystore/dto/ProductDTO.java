package com.brocamp.babystore.dto;

import com.brocamp.babystore.model.Brand;
import com.brocamp.babystore.model.Category;
import lombok.Data;

import java.util.List;
@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private double costPrice;
    private double salePrice;
    private long currentQuantity;
    private List<String> imageUrls;
    private Category category;
    private Brand brand;
    private boolean productDeleted;
    private boolean productActivated;
}
