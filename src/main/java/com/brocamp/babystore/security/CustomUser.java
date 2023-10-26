package com.brocamp.babystore.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Date;

@Getter
@Setter
public class CustomUser extends User {
    private long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean isDelete;
    private boolean isBlocked;
    private boolean isActive;
    private Date createdAt;
    private Date updateOn;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
                      long id,String firstName,String lastName,String phoneNumber,boolean isDelete,boolean isBlocked,
                      boolean isActive,Date createdAt,Date updateOn) {
        super(username, password, authorities);
        this.id =id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.isDelete = isDelete;
        this.isBlocked = isBlocked;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updateOn = updateOn;
    }
}
