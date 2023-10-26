package com.brocamp.babystore.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
@Data
public class ProductDto {
    private Long id;

    @NotNull(message = "is required")
    @Size(min=5, max=30,message = "Please enter full name")
    @Pattern(regexp="^[A-Za-z ]+$",message = "No special characters and numbers are allowed")
    private String name;
    private String description;
    private double costPrice;
    private double salePrice;
    private long currentQuantity;
    private Brand brand;
    private Category category;
    private boolean productDeleted;
    private boolean productActivated;
    private List<String> imageUrls;
}
