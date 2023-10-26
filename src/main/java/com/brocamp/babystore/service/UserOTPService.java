package com.brocamp.babystore.service;

import com.brocamp.babystore.model.UserOTP;

public interface UserOTPService {
    void saveOrUpdate(UserOTP userOTP);

    boolean existsByEmail(String email);

    UserOTP findByEmail(String email);
}
