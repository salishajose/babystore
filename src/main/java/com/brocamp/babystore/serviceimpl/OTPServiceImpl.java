package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.service.OTPService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OTPServiceImpl implements OTPService {
    @Override
    public String generateOTP() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}
