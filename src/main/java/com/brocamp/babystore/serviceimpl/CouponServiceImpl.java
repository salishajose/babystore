package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.dto.CouponDTO;
import com.brocamp.babystore.model.Coupon;
import com.brocamp.babystore.repository.CouponRepository;
import com.brocamp.babystore.service.CouponService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class CouponServiceImpl implements CouponService {
    private CouponRepository couponRepository;
    @Override
    public void saveCoupon(CouponDTO couponDTO) throws Exception {
        Coupon coupon = new Coupon();
        coupon.setCouponCode(couponDTO.getCouponCode());
        coupon.setDescription(couponDTO.getDescription());
        coupon.setMaximumAmount(couponDTO.getMaximumAmount());
        coupon.setMinimumOrderAmount(couponDTO.getMinimumOrderAmount());
        coupon.setCount(couponDTO.getCount());
        coupon.setDiscount(couponDTO.getDiscount());
        coupon.setExpiryDate(couponDTO.getExpiryDate());
        coupon.setActivated(true);
        coupon.setCreatedAt(new Date());
        coupon.setUpdateOn(new Date());
        try{
            couponRepository.save(coupon);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("Couldn't save coupon");
        }
    }

    @Override
    public List<Coupon> findActiveCoupon() throws Exception {
        try{
            Optional<List<Coupon>> optionalCouponList = couponRepository.findActiveCoupon();
            return optionalCouponList.orElse(new ArrayList<Coupon>());
        }catch(Exception e){
            System.out.println(e.getMessage());
            throw new Exception("COudn'tfetch coupon details");
        }
    }

    @Override
    public void activateCoupon(long id) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(id);
        Coupon coupon = optionalCoupon.orElse(null);
        if(coupon!=null){
            coupon.setActivated(true);
            coupon.setUpdateOn(new Date());
            couponRepository.save(coupon);
        }
    }

    @Override
    public void deactivateCoupon(long id) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(id);
        Coupon coupon = optionalCoupon.orElse(null);
        if(coupon!=null){
            coupon.setActivated(false);
            coupon.setUpdateOn(new Date());
            couponRepository.save(coupon);
        }
    }

    @Override
    public void deleteCoupon(long id) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(id);
        Coupon coupon = optionalCoupon.orElse(null);
        if(coupon!=null){
            coupon.setDeleted(true);
            coupon.setUpdateOn(new Date());
            couponRepository.save(coupon);
        }
    }

    @Override
    public Coupon findById(long couponId) {
        return couponRepository.findById(couponId).orElseThrow();
    }

    @Override
    public List<Coupon> findCurrentCoupons(Double cartTotal, LocalDate date) {
        return couponRepository.findCurrentCoupons(cartTotal,date);
    }

    @Override
    public void updateCoupon(CouponDTO couponDTO) throws Exception {
        Coupon coupon = couponRepository.findById(couponDTO.getId()).orElseThrow();
        coupon.setCouponCode(couponDTO.getCouponCode());
        coupon.setDescription(couponDTO.getDescription());
        coupon.setMaximumAmount(couponDTO.getMaximumAmount());
        coupon.setMinimumOrderAmount(couponDTO.getMinimumOrderAmount());
        coupon.setCount(couponDTO.getCount());
        coupon.setDiscount(couponDTO.getDiscount());
        coupon.setExpiryDate(couponDTO.getExpiryDate());
        coupon.setActivated(true);
        coupon.setCreatedAt(new Date());
        coupon.setUpdateOn(new Date());
        try{
            couponRepository.save(coupon);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("Couldn't save coupon");
        }
    }

    @Override
    public Double checkCouponApplicableAMount(long couponId, double cartTotal) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                ()->new RuntimeException("COuldn't fetch coupon details of id : "+couponId));
        double oldDiscount = cartTotal*(coupon.getDiscount()/100);
        String formattedDiscount = String.format("%.2f",oldDiscount);
        Double discount= Double.parseDouble(formattedDiscount);
        if(discount>coupon.getMaximumAmount()){
            discount = coupon.getMaximumAmount();
        }
        double oldFinalAmount = cartTotal-discount;

        String formattedoldFinalAmount = String.format("%.2f",oldFinalAmount);
        Double finalAmount= Double.parseDouble(formattedoldFinalAmount);

        return finalAmount;
    }
}
