package com.brocamp.babystore.service;

import com.brocamp.babystore.model.Users;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;


public interface EmailService {

    String sendSimpleMail(String email, String otp);

    void sendReferralLink(String email, String referralLink) throws MessagingException, UnsupportedEncodingException;
}
