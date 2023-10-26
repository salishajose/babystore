package com.brocamp.babystore.controller;

import com.brocamp.babystore.model.UserOTP;
import com.brocamp.babystore.repository.UsersRepository;
import com.brocamp.babystore.service.EmailService;
import com.brocamp.babystore.service.OTPService;
import com.brocamp.babystore.service.UserOTPService;
import com.brocamp.babystore.service.UsersSevice;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
public class EmailSendController {
    private OTPService otpService;
    private EmailService emailService;
    private UserOTPService userOTPService;

    private UsersSevice usersSevice;
    private PasswordEncoder passwordEncoder;

    public EmailSendController(OTPService otpService,
                               EmailService emailService,
                               UserOTPService userOTPService,
                               UsersSevice usersSevice,
                               PasswordEncoder passwordEncoder) {
        this.otpService = otpService;
        this.emailService = emailService;
        this.userOTPService = userOTPService;
        this.usersSevice = usersSevice;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sendVerificationEmailOtp")
    public String sendVerificationEmailOtp(
            @RequestParam("email")String email
                        , HttpSession session,
            RedirectAttributes redirectAttributes) throws Exception {

        if(!usersSevice.existsByEmail(email)){
            String otp = otpService.generateOTP();
            if(!userOTPService.existsByEmail(email)){
                // new email verification
                UserOTP userOTP =new UserOTP();
                userOTP.setEmail(email);
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setCreatedAt(new Date());
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
                try{
                    userOTPService.saveOrUpdate(userOTP);
                }catch(Exception e){
                    e.printStackTrace();
                    throw new Exception("Couldn't finish OTP verification process"+ HttpStatus.BAD_REQUEST);
                }

            }else{
                //code to delete all data related to this email id
                UserOTP userOTP=userOTPService.findByEmail(email);
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
                try{
                    userOTPService.saveOrUpdate(userOTP);
                }catch(Exception e){
                    e.printStackTrace();
                    throw new Exception("Couldn't finish OTP verification process");
                }
            }
            String status = emailService.sendSimpleMail(email,otp);
            if(status.equals("success")){
                session.setAttribute("message","otpsent");
                redirectAttributes.addFlashAttribute("email",email);
                return "redirect:/otpvalidation";

            }else{
                return "redirect:/verifyEmail?error";
            }
        }else{
            return "redirect:/verifyEmail?existUser";
        }

    }
    @PostMapping("/sendEmailOTPLogin")
    public String sendEmailOTPLogin(
            @RequestParam("email")String email
            , HttpSession session,
            RedirectAttributes redirectAttributes) throws Exception {
        if(usersSevice.existsByEmail(email)){
            String otp = otpService.generateOTP();
            UserOTP userOTP = userOTPService.findByEmail(email);
            if(userOTP!=null){
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
            }else{
                userOTP = new UserOTP();
                userOTP.setEmail(email);
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setCreatedAt(new Date());
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
            }
            try{
                userOTPService.saveOrUpdate(userOTP);
            }catch(Exception e){
                e.printStackTrace();
                throw new Exception("Send OTP.Please try after some time...");
            }
            String status = emailService.sendSimpleMail(email,otp);
            if(status.equals("success")){
                session.setAttribute("message","otpsent");
                redirectAttributes.addFlashAttribute("email",email);
                return "redirect:/forgotPasswordOTPLogin";

            }else{
                return "redirect:/forgotpassword?error";
            }
        }else{
            return "redirect:/forgotpassword?error";
        }
    }
}
