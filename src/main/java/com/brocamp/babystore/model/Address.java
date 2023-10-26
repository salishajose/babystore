package com.brocamp.babystore.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="address_id")
    private long id;
    private String recipientName;
    private String houseDetails;
    private String streetAddress;
    private String landmark;
    private String pinCode;
    private String city;
    private String state;
    private String phoneNumber;
    private String alternatePhoneNUmber;
    private String typeOfAddress;
    private boolean deleted;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="users_id",referencedColumnName = "users_id")
    private Users users;
}
