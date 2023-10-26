package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.model.Users;
import com.brocamp.babystore.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }



    private String generateEmailOtpVarificationMessage(String otp) {
        String message ="Hello Customer "
                +"For email verification you need to enter OTP.One Time Password for verification is: "+otp
                +" Note: this OTP is set to expire in 5 minutes.";
        return message;
    }


    @Override
    public String sendSimpleMail(String email, String otp) {
        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();
            String message=generateEmailOtpVarificationMessage(otp);
            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setText(message);
            mailMessage.setSubject("Email Varification for MyBabyStore");

            // Sending the mail
            javaMailSender.send(mailMessage);

            return "success";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @Override
    public void sendReferralLink(String recipientEmail, String referralLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(sender,"Baby store");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>Here is the Baby Store sign up link .</p>"
                + "<p>Click the link below to sign up:</p>"
                + "<p><a href=\"" + referralLink + "\">SIgn Up Baby Store</a></p>"
                + "<br>"
                + "<p>Ignore this email if you already have an account, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        javaMailSender.send(message);
    }

}

