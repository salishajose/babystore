package com.brocamp.babystore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class UsersDTO {
    private long id;
    @NotBlank
    @Size(min=5, max=30,message = "Please enter full name")
    @Pattern(regexp="^[A-Za-z ]+$",message = "No special characters and numbers are allowed")
    private String firstName;
    @NotBlank
    @Size(min=5, max=30,message = "Please enter full name")
    @Pattern(regexp="^[A-Za-z ]+$",message = "No special characters and numbers are allowed")
    private String lastName;
    @NotBlank
    @Pattern(regexp = "^[789]\\d{9}$", message = "Please provide a valid Indian phone number start with 789")
    private String phoneNumber;
    @NotBlank
    private String email;
    private String role;
    private String password;
    private String confirmPassword;
    private boolean isDelete;
    private boolean isBlocked;
    private boolean isActive;
    private Date createdAt;
    private Date updateOn;
}
