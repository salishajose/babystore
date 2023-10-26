package com.brocamp.babystore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;
@Data
public class CategoryDTO {
    private long id;
    @NotEmpty
    @NotBlank(message = "Category name must be filled")
    @Pattern(regexp="^[a-zA-Z0-9 ]+$",message = "No special charaters are allowed")
    private String name;
    private boolean isDelete;
    private Date createdAt;
    private Date updateOn;
}
