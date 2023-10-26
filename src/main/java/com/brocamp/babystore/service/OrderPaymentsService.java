package com.brocamp.babystore.service;

import com.brocamp.babystore.model.OrderPayments;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

public interface OrderPaymentsService {
    Order createRazorPayOrder(double amount, long orderDetailsId) throws RazorpayException;

    void updateOrderPayment(String paymentId, String orderId, String status);

    OrderPayments findByOrderDetails(long id);

    double findPaidRevenue();

    Double findMonthlyPaidRevenue();
}
