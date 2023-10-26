package com.brocamp.babystore.controller;

import com.brocamp.babystore.dto.PasswordDTO;
import com.brocamp.babystore.dto.UsersDTO;
import com.brocamp.babystore.model.UserOTP;
import com.brocamp.babystore.model.Users;
import com.brocamp.babystore.repository.UsersRepository;
import com.brocamp.babystore.security.CustomUser;
import com.brocamp.babystore.service.EmailService;
import com.brocamp.babystore.service.ReferralOfferService;
import com.brocamp.babystore.service.UserOTPService;
import com.brocamp.babystore.service.UsersSevice;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@AllArgsConstructor
public class UsersController {
    private UsersSevice usersSevice;
    private EmailService emailService;
    private UserOTPService userOTPService;
    private PasswordEncoder passwordEncoder;
    private ReferralOfferService referralOfferService;


    @PostMapping("user-registration")
    public String userRegistration(@ModelAttribute("newUsers")Users newUser,
                                   BindingResult bindingResult, HttpSession httpSession) throws Exception {
        if(usersSevice.findAlreadyExistUserByEmail(newUser.getEmail())){
            bindingResult.rejectValue("email",null,"There exists user with this username");
        }
        if(bindingResult.hasErrors()){
            return "redirect:/signup?error";
        }else{
            try{
                String encodedPassword = passwordEncoder.encode(newUser.getPassword());
                newUser.setPassword(encodedPassword);
                newUser.setRole("CUSTOMER");
                newUser.setActive(true);
                newUser.setCreatedAt(new Date());
                newUser.setUpdateOn(new Date());
                usersSevice.saveOrUpdate(newUser);
                UserOTP userOTP = userOTPService.findByEmail(newUser.getEmail());
                if(userOTP!=null){
                    userOTP.setOneTimePassword(null);
                    userOTP.setOtpRequestedTime(null);
                    userOTP.setUpdateOn(new Date());
                    userOTPService.saveOrUpdate(userOTP);
                }
                httpSession.setAttribute("message","OTP is send to registered email.Please enter the Message within 5 minutes");
                return "redirect:/signup?sendEmailOtp";
            }catch (Exception e){
                e.printStackTrace();
                throw new Exception("Can not save customer details");

            }

        }
    }
    @GetMapping("admin_panel/user_management")
    public String findAllUsers(Model model,Authentication authentication) throws Exception {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser == null){
            return "redirect:/login";
        }
        if(customUser.isBlocked()){
            return "redirect:/login";
        }
        try{
            int pageSize =5,pageNo=1;
            Page<Users>  usersPage = usersSevice.findPaginated(pageNo,pageSize);
            List<Users> usersList = usersPage.getContent();
            model.addAttribute("usersList",usersList);


            model.addAttribute("currentPage",pageNo);
            model.addAttribute("totalPages",usersPage.getTotalPages());
            model.addAttribute("totalItems",usersPage.getTotalElements());

            model.addAttribute("title", "users");
            model.addAttribute("users", usersList);
            model.addAttribute("size", usersList.size());
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Can not fetch details" +HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return "admin/viewAllUsers";
    }
    @GetMapping("/admin_panel/user_management/page/{pageNo}")
    public String showPaginatedUsers(@PathVariable int pageNo,
                                     Model model,
                                     Authentication authentication){
        int pageSize =5;
        Page<Users> usersPage = usersSevice.findPaginated(pageNo,pageSize);
        List<Users> usersList = usersPage.getContent();
        model.addAttribute("usersList",usersList);


        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",usersPage.getTotalPages());
        model.addAttribute("totalItems",usersPage.getTotalElements());

        model.addAttribute("title", "users");
        model.addAttribute("users", usersList);
        model.addAttribute("size", usersList.size());
        return "admin/viewAllUsers";
    }
    @GetMapping("/admin_panel/user_management/block/{id}")
    public String blockUser(@PathVariable long id) throws Exception {
        try{
            if(usersSevice.existById(id)){
                Users users = usersSevice.findById(id);
                users.setBlocked(true);
                users.setUpdateOn(new Date());
                try{
                    usersSevice.saveOrUpdate(users);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new Exception("Internal server error");
                }
            }else{
                throw new Exception("User Not found  with this id = "+id);
            }
            //Optional<Users> optionalUsers =Optional.ofNullable((Users) usersSevice.findById(id));
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Internal servererror");
        }
        return "redirect:/admin_panel/user_management";
    }
    @GetMapping("/admin_panel/user_management/unblock/{id}")
    public String unblockUser(@PathVariable long id) throws Exception {
        try{
            if(usersSevice.existById(id)){
                Users users = usersSevice.findById(id);
                users.setBlocked(false);
                users.setUpdateOn(new Date());
                try{
                    usersSevice.saveOrUpdate(users);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new Exception("Internal server error");
                }
            }else{
                throw new Exception("User Not found  with this id = "+id);
            }
            //Optional<Users> optionalUsers =Optional.ofNullable((Users) usersSevice.findById(id));
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Internal servererror");
        }
        return "redirect:/admin_panel/user_management";
    }
    @GetMapping("/user_home/user/view")
    public String showUserDetails(Authentication authentication,Model model){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setId(customUser.getId());
        usersDTO.setFirstName(customUser.getFirstName());
        usersDTO.setLastName(customUser.getLastName());
        usersDTO.setPhoneNumber(customUser.getPhoneNumber());
        usersDTO.setEmail(customUser.getUsername());
        model.addAttribute("usersDTO",usersDTO);
        return "user/myProfile";
    }
    @PostMapping("/user_home/users/update")
    public String saveUserProfile(@Valid UsersDTO usersDTO,BindingResult bindingResult,Model model){

        System.out.println(usersDTO);
        if(bindingResult.hasErrors()){
            model.addAttribute("usersDTO",usersDTO);
            return "user/myProfile";
        }else{
            //code to update user details...
            usersSevice.updateUserProfile(usersDTO);
            return "redirect:/user_home/user/view";
        }
    }
    @GetMapping("/user_home/user/changePassword")
    public String showChangePasswordPage(Authentication authentication,
                                         Model model){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setId(customUser.getId());
        model.addAttribute("passwordDTO",passwordDTO);
        model.addAttribute("mismatch"," ");
        return "user/changePassword";
    }
    @PostMapping("/user_home/user/changePassword")
    public String ChangePassword(@Valid PasswordDTO passwordDTO,
                                 BindingResult bindingResult,Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("passwordDTO",passwordDTO);
            model.addAttribute("mismatch"," ");
            return "user/changePassword";
        }else if(!passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())){
            model.addAttribute("mismatch","Password mismatches.Please confirm..");
            model.addAttribute("passwordDTO",passwordDTO);
            return "user/changePassword";
        }
        else{
            //code to rest password
            usersSevice.changePassword(passwordDTO);
            model.addAttribute("mismatch","Password Changed");
            model.addAttribute("passwordDTO",passwordDTO);
            return "user/changePassword";
        }

    }
    @PostMapping("user-registration/referral")
    public String saveReferralUser(@ModelAttribute("newUsers")Users newUser,
                                   BindingResult bindingResult, HttpSession httpSession) throws Exception {
        if(usersSevice.findAlreadyExistUserByEmail(newUser.getEmail())){
            bindingResult.rejectValue("email",null,"There exists user with this username");
        }
        if(bindingResult.hasErrors()){
            return "redirect:/signup?error";
        }else{
            try{
                String encodedPassword = passwordEncoder.encode(newUser.getPassword());
                newUser.setPassword(encodedPassword);
                newUser.setRole("CUSTOMER");
                newUser.setActive(true);
                newUser.setCreatedAt(new Date());
                newUser.setUpdateOn(new Date());
                usersSevice.saveOrUpdate(newUser);
                UserOTP userOTP = userOTPService.findByEmail(newUser.getEmail());
                if(userOTP!=null){
                    userOTP.setOneTimePassword(null);
                    userOTP.setOtpRequestedTime(null);
                    userOTP.setUpdateOn(new Date());
                    userOTPService.saveOrUpdate(userOTP);
                }
                httpSession.setAttribute("message","User registered successfully");
                referralOfferService.addReferralAmount(newUser.getEmail());
                return "redirect:/signup?sendEmailOtp";
            }catch (Exception e){
                e.printStackTrace();
                throw new Exception("Can not save customer details");

            }

        }
    }
}
