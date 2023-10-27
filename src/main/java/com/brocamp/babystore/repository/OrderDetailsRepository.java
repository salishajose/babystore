package com.brocamp.babystore.repository;

import com.brocamp.babystore.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails,Long> {
    @Query("select od from OrderDetails od where od.users.id=?1")
    Optional<List<OrderDetails>> findByUsersId(long usersId);

    @Query("select coalesce(SUM(o.orderAmount),0)  from OrderDetails o where o.orderStatus='DELIVERED' and o.paymentMethods.id=1")
    double findTotalDeliveredCODRevenue();

    @Query("select COUNT(o) from OrderDetails o where o.orderStatus=?1")
    long findTotalOrdersByOrderStatus(String orderStatus);
//
//    @Query("select SUM(o.orderAmount) from OrderDetails o where o.orderStatus='DELIVERED'" +
//            " and o.paymentMethods.id=1 and to_char(cast(o.orderDate as date )),'Month')=to_char(now(),'Month') and" +
//            " to_char(cast(o.orderDate as date )),'Year')=to_char(now(),'Year')")
    @Query("SELECT COALESCE(SUM(o.orderAmount),0) FROM OrderDetails o " +
        "WHERE o.orderStatus = 'DELIVERED' " +
        "AND o.paymentMethods.id = 1 " +
        "AND EXTRACT(MONTH FROM o.deliveryDate) = EXTRACT(MONTH FROM CURRENT_DATE) " +
        "AND EXTRACT(YEAR FROM o.deliveryDate) = EXTRACT(YEAR FROM CURRENT_DATE)")

    Double findMonthlyDeliveredCODRevenue();
    @Query("select COALESCE(count(o),0) from OrderDetails o where o.couponId=?1 and o.users.id=?2 " +
            " and o.orderStatus!='CANCELLED'")
    long findAppliedCoupon(long couponId, long usersId);

    @Query("SELECT FORMAT(o.orderDate, 'yyyy-MM') AS month,  SUM(o.orderAmount) AS totalAmount FROM OrderDetails o GROUP BY  FORMAT(o.orderDate, 'yyyy-MM')")
    List<Object[]> getMonthlySaleChartDetails();
}
