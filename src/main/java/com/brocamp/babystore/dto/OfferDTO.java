package com.brocamp.babystore.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;

@Data
public class OfferDTO {
    private Long id;

    private String name;

    private String description;

    private int offPercentage;

    private String offerType;

    private Long offerProductId;
    private String applicableForProductName;
    private Long offerCategoryId;
    private String applicableForCategoryName;

    private boolean enabled;
    private boolean deleted;
    private Date createdAt;
    private Date updateOn;
}
