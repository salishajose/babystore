package com.brocamp.babystore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String role;
    private String password;
    private boolean isDelete;
    private boolean isBlocked;
    private boolean isActive;
    private Date createdAt;
    private Date updateOn;

}
