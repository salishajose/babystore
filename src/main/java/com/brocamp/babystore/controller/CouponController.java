package com.brocamp.babystore.controller;

import com.brocamp.babystore.dto.CouponDTO;
import com.brocamp.babystore.model.Coupon;
import com.brocamp.babystore.service.CouponService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
public class CouponController {
    private CouponService couponService;
    @GetMapping("/admin_panel/coupon/add")
    public String showADDcouponPage(CouponDTO couponDTO, Model model){
        LocalDate minimumDate=LocalDate.now().plusDays(1);
        model.addAttribute("minimumDate",minimumDate);
        return "admin/addCoupon";
    }
    @GetMapping("/admin_panel/coupon")
    public String showALlCoupon(Model model) throws Exception {
        List<Coupon> couponList = couponService.findActiveCoupon();
        model.addAttribute("couponList",couponList);
        return "admin/viewAllCoupon";
    }
    @PostMapping("/admin_panel/coupon/add")
    public String saveCoupon(@Valid CouponDTO couponDTO,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) throws Exception {
        if(bindingResult.hasErrors()){
            LocalDate minimumDate=LocalDate.now().plusDays(1);
            model.addAttribute("minimumDate",minimumDate);
            return "admin/addCoupon";
        }else{
            //code to save coupon
            try{
                couponService.saveCoupon(couponDTO);
                redirectAttributes.addFlashAttribute("success", "Added new Coupon successfully!");

            }catch (Exception e){
                System.out.println(e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Failed to add new Coupon!");
            }
            return "redirect:/admin_panel/coupon";
        }
    }
    @GetMapping("/admin_panel/coupon/activate/{id}")
    public String activateCOupon(@PathVariable long id){
        couponService.activateCoupon(id);
        return "redirect:/admin_panel/coupon";
    }
    @GetMapping("/admin_panel/coupon/deactivate/{id}")
    public String deactivateCOupon(@PathVariable long id){
        couponService.deactivateCoupon(id);
        return "redirect:/admin_panel/coupon";
    }
    @GetMapping("/admin_panel/coupon/delete/{id}")
    public String deleteCOupon(@PathVariable long id){
        couponService.deleteCoupon(id);
        return "redirect:/admin_panel/coupon";
    }
    @GetMapping("/admin_panel/coupon/update/{id}")
    public String updateCoupon(@PathVariable long id,Model model){
        Coupon coupon = couponService.findById(id);
        CouponDTO couponDTO = new CouponDTO();
        couponDTO.setId(coupon.getId());
        couponDTO.setCouponCode(coupon.getCouponCode());
        couponDTO.setDiscount(coupon.getDiscount());
        couponDTO.setCount(coupon.getCount());
        couponDTO.setDescription(coupon.getDescription());
        couponDTO.setExpiryDate(coupon.getExpiryDate());
        couponDTO.setMaximumAmount(coupon.getMaximumAmount());
        couponDTO.setMinimumOrderAmount(coupon.getMinimumOrderAmount());
        model.addAttribute("couponDTO",couponDTO);

        LocalDate minimumDate=LocalDate.now().plusDays(1);
        model.addAttribute("minimumDate",minimumDate);
        return "admin/updateCoupon";
    }
    @PostMapping("/admin_panel/coupon/update/{id}")
    public String updateCoupon(@PathVariable long id,
                               @Valid CouponDTO couponDTO,
                               BindingResult bindingResult,
                               Model model,RedirectAttributes redirectAttributes) throws Exception {
        if(bindingResult.hasErrors()){
            LocalDate minimumDate=LocalDate.now().plusDays(1);
            model.addAttribute("minimumDate",minimumDate);
            return "admin/addCoupon";
        }else{
            //code to update coupon
            couponDTO.setId(id);
            try{
                couponService.updateCoupon(couponDTO);
                redirectAttributes.addFlashAttribute("success", "Updated successfully!");
            }catch(Exception e){
                System.out.println(e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
            }
            return "redirect:/admin_panel/coupon";
        }
    }
    @PostMapping("/user_home/coupon/checkCartAmount")
    @ResponseBody
    public double checkCouponANdCart(@RequestBody String data){
        JSONObject jsonData = new JSONObject(data);
        long couponId = jsonData.getLong("couponId");
        double cartTotal = jsonData.getDouble("cartTotal");
        Double finalCartAmount = couponService.checkCouponApplicableAMount(couponId,cartTotal);
        return finalCartAmount;
    }
}
