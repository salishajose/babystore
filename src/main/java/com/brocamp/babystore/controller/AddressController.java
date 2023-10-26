package com.brocamp.babystore.controller;

import com.brocamp.babystore.dto.AddressDTO;
import com.brocamp.babystore.dto.OrderDetailsDTO;
import com.brocamp.babystore.model.Address;
import com.brocamp.babystore.model.Coupon;
import com.brocamp.babystore.model.PaymentMethods;
import com.brocamp.babystore.model.Users;
import com.brocamp.babystore.repository.ShoppingCartRepository;
import com.brocamp.babystore.security.CustomUser;
import com.brocamp.babystore.service.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class AddressController {
    private AddressService addressService;
    private UsersSevice usersSevice;
    private PaymentMethodsService paymentMethodsService;

    private CouponService couponService;
    private ShoppingCartService shoppingCartService;
    private OrderDetailsService orderDetailsService;
    @GetMapping("/user_home/address")
    public String findAllAddress(OrderDetailsDTO orderDetailsDTO,
                                 Authentication authentication,
                                 Model model) throws Exception {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser==null){
            return "redirect:/login";
        }
        if(customUser.isBlocked()){
            return "redirect:/login";
        }else{
            try {
                List<Address> addressList = addressService.findAllByUsersId(customUser.getId());
                List<PaymentMethods> paymentMethodsList = paymentMethodsService.findAllPaymentMethods();
                model.addAttribute("paymentMethodsList",paymentMethodsList);
                model.addAttribute("addressList", addressList);
                model.addAttribute("orderDetailsDTO",new OrderDetailsDTO());
                //code to get current cart total
                List<Object[]> obj = shoppingCartService.getQuantitySumAndTotalRateSum(customUser.getId());
                Double cartTotal = 0.0;
                for (Object[] ob : obj) {
                    long totalQuantity = (Long) ob[0];
                    cartTotal = (Double) ob[1];
                    model.addAttribute("totalQuantity", totalQuantity);
                    model.addAttribute("cartTotal", cartTotal);
                }
                //code to load coupons
                List<Coupon> couponList = couponService.findCurrentCoupons(cartTotal, LocalDate.now());
                if(!couponList.isEmpty()){
                    List<Coupon> foundCoupons = new ArrayList<>();
                    for(Coupon coupon : couponList){
                        long count = orderDetailsService.findAppliedCoupon(coupon.getId(),customUser.getId());
                        if(count==coupon.getCount()){
                            foundCoupons.add(coupon);
                        }
                    }
                    couponList.removeAll(foundCoupons);
                }
                model.addAttribute("couponList",couponList);
                model.addAttribute("cartTotal",cartTotal);
                return "user/page-address";
            }catch(Exception e){
                e.printStackTrace();
                throw new Exception("Can not fetch data from database");
            }
        }

    }
    @GetMapping("/user_home/address/add")
    public String showAddress(AddressDTO addressDTO) {
        //model.addAttribute("addressDTO",new AddressDTO());
        return "user/addAddress";
    }
        @PostMapping("/user_home/address/add")
    public String saveUserAddress(@Valid AddressDTO addressDTO,
                                  BindingResult theBindingResult,Authentication authentication){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(theBindingResult.hasErrors()){
            return "user/addAddress";
        }
        else {
            //code to save address
            try {
                Users users = usersSevice.findById(customUser.getId());
                addressService.saveAddress(addressDTO,users);
            }catch (Exception e){
                e.printStackTrace();
            }
            return "user/page-address";
        }
    }
    @GetMapping("/user_home/address/all")
    public String findAllUsersAddress(OrderDetailsDTO orderDetailsDTO,
                                 Authentication authentication,
                                 Model model) throws Exception {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser==null){
            return "redirect:/login";
        }else{
            try {
                List<Address> addressList = addressService.findAllByUsersId(customUser.getId());
                model.addAttribute("addressList", addressList);
                return "user/myAdresses";
            }catch(Exception e){
                e.printStackTrace();
                throw new Exception("Can not fetch data from database");
            }
        }

    }
    @GetMapping("/user_home/address/delete/{id}")
    public String deleteAddress(@PathVariable long id){
        addressService.deleteById(id);
        return "redirect:/user_home/address/all";
    }
    @GetMapping("/user_home/address/update/{id}")
    public String editAddress(@PathVariable long id,Model model){
        AddressDTO addressDTO = addressService.findById(id);
        model.addAttribute("addressDTO",addressDTO);
        return "user/updateAddress";
    }
    @PostMapping("/user_home/address/update/{id}")
    public String updateAddress(@PathVariable long id,
                                @Valid AddressDTO addressDTO,
                                BindingResult bindingResult,
                                Authentication authentication,
                                Model model){
        addressDTO.setId(id);
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(bindingResult.hasErrors()){
            model.addAttribute("addressDTO",addressDTO);
            return "user/updateAddress";
        }
        else {
            //code to update address
            addressService.updateAddress(addressDTO);
        }
        return "redirect:/user_home/address/all";
    }
}
