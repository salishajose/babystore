package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.model.UserOTP;
import com.brocamp.babystore.repository.UserOTPRepository;
import com.brocamp.babystore.service.UserOTPService;
import org.springframework.stereotype.Service;

@Service
public class UserOTPServiceImpl implements UserOTPService {
    private UserOTPRepository userOTPRepository;

    public UserOTPServiceImpl(UserOTPRepository userOTPRepository) {
        this.userOTPRepository = userOTPRepository;
    }

    @Override
    public void saveOrUpdate(UserOTP userOTP) {
        userOTPRepository.save(userOTP);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userOTPRepository.existsByEmail(email);
    }

    @Override
    public UserOTP findByEmail(String email) {
        return userOTPRepository.findByEmail(email);
    }
}
