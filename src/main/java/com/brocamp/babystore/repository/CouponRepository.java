package com.brocamp.babystore.repository;

import com.brocamp.babystore.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon,Long> {
    @Query("from Coupon c where c.deleted=false")
    Optional<List<Coupon>> findActiveCoupon();
    @Query("select c from Coupon c where c.deleted=false and c.activated=true and " +
            " c.minimumOrderAmount<?1 and c.expiryDate>=?2")
    List<Coupon> findCurrentCoupons(Double cartTotal, LocalDate date);
}
