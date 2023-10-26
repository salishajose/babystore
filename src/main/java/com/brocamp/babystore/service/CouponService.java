package com.brocamp.babystore.service;

import com.brocamp.babystore.dto.CouponDTO;
import com.brocamp.babystore.model.Coupon;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CouponService {
    void saveCoupon(CouponDTO couponDTO) throws Exception;

    List<Coupon> findActiveCoupon() throws Exception;

    void activateCoupon(long id);

    void deactivateCoupon(long id);

    void deleteCoupon(long id);

    Coupon findById(long couponId);

    List<Coupon> findCurrentCoupons(Double cartTotal, LocalDate date);

    void updateCoupon(CouponDTO couponDTO) throws Exception;

    Double checkCouponApplicableAMount(long couponId, double cartTotal);
}
