package com.brocamp.babystore.dto;

import com.brocamp.babystore.model.Users;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
public class AddressDTO {

    private long id;
    @NotNull(message = "is required")
    @Size(min=5, max=30,message = "Please enter full name")
    @Pattern(regexp="^[A-Za-z ]+$",message = "No special characters and numbers are allowed")
    private String recipientName;
    @NotNull(message = "is required")
    @Size(min=5, max=30,message = "Minimum 5-30 charaters required")
    @Pattern(regexp="^[a-zA-Z0-9 ]+$",message = "No special charaters are allowed")
    private String houseDetails;

    @NotNull(message = "is required")
    @Size(min=5, max=30,message = "Minimum 5-30 charaters required")
    @Pattern(regexp="^[a-zA-Z0-9 ]+$",message = "No special charaters are allowed")
    private String streetAddress;

    @Pattern(regexp="^[a-zA-Z0-9 ]+$",message = "No special charaters are allowed")
    @NotNull(message = "is required")
    @Size(min=5, max=30,message = "Minimum 5-30 charaters required")
    private String landmark;

    @Size(min=5, max=30,message = "Minimum 5-30 charaters required")
    @NotNull(message = "is required")
    @Pattern(regexp="^[0-9]{6}",message = "Invalid Pincode")
    private String pinCode;

    @Size(min=5, max=30,message = "Minimum 5-30 charaters required")
    @NotNull(message = "is required")
    @Pattern(regexp="^[a-zA-Z ]+$",message = "No special charaters are allowed")
    private String city;

    @Size(min=3, max=30,message = "Minimum 5-30 charaters required")
    @NotNull(message = "is required")
    @Pattern(regexp="^[a-zA-Z ]+$",message = "No special charaters are allowed")
    private String state;

    @NotNull(message = "is required")
    @Pattern(regexp = "^[789]\\d{9}$", message = "Please provide a valid Indian phone number start with 789")
    private String phoneNumber;

    @NotNull(message = "is required")
    @Pattern(regexp = "^[789]\\d{9}$", message = "Please provide a valid Indian phone number start with 789")
    private String alternatePhoneNUmber;

    @NotNull(message = "is required")
    private String typeOfAddress;
    private boolean deleted;
    private Users users;
}
