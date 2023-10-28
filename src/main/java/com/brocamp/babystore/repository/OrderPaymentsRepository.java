package com.brocamp.babystore.repository;

import com.brocamp.babystore.model.OrderPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.sql.Date;
import java.util.List;

public interface OrderPaymentsRepository extends JpaRepository<OrderPayments,Long> {
    OrderPayments findByOrderId(String orderId);

    @Query("from OrderPayments o where o.orderDetails.id=?1")
    OrderPayments findByOrderDetails(long id);

    @Query("select COALESCE(SUM(o.amount),0) from OrderPayments o where o.status='Paid'")
    double findPaidRevenue();
    @Query("SELECT COALESCE(SUM(o.amount),0) FROM OrderPayments o " +
            "WHERE o.status = 'Paid' " +
            "AND EXTRACT(MONTH FROM o.updateOn) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM o.updateOn) = EXTRACT(YEAR FROM CURRENT_DATE)")
    Double findMonthlyPaidRevenue();

    @Query("select op from OrderPayments op where op.status='created' and op.orderDetails.users.id=?1")
    List<OrderPayments> findByOrderStatusAndPaymentStatus(long usersId);
}
