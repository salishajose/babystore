package com.brocamp.babystore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Date;
@Data
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="category_id")
    private long id;
    @NotEmpty(message = "Category name required")
    private String name;
    private boolean isDelete;
    private Date createdAt;
    private Date updateOn;
    private boolean isBlocked;
}
