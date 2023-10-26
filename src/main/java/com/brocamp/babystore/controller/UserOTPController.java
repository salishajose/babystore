package com.brocamp.babystore.controller;

import com.brocamp.babystore.model.UserOTP;
import com.brocamp.babystore.service.UserOTPService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserOTPController {
    private UserOTPService userOTPService;
    private PasswordEncoder passwordEncoder;

    public UserOTPController(UserOTPService userOTPService, PasswordEncoder passwordEncoder) {
        this.userOTPService = userOTPService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/validateOTP")
    public String validateOTP(@ModelAttribute("userOTP")UserOTP userOTPRequest, HttpSession session,
                              RedirectAttributes redirectAttributes){
        String email=session.getAttribute("email").toString();
        UserOTP userOTP = userOTPService.findByEmail(userOTPRequest.getEmail());

        if(passwordEncoder.matches(userOTPRequest.getOneTimePassword(),userOTP.getOneTimePassword())){
            //navigate to signup page
            redirectAttributes.addFlashAttribute("email",userOTP.getEmail());
            return "redirect:/signup";
        }else{
            return "redirect:/otpvalidation?error";
        }

    }
}
