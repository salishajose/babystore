package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.model.OrderDetails;
import com.brocamp.babystore.model.OrderPayments;
import com.brocamp.babystore.repository.OrderDetailsRepository;
import com.brocamp.babystore.repository.OrderPaymentsRepository;
import com.brocamp.babystore.service.OrderPaymentsService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class OrderPaymentsServiceImpl implements OrderPaymentsService {
    private static  String key="rzp_test_ceHqYFxzbLUSlS";
    private static String key_secret="RMJ6wJ6O4vDhpIN2VRlhOSq1";
    private OrderPaymentsRepository orderPaymentsRepository;
    private OrderDetailsRepository orderDetailsRepository;
    @Override
    public Order createRazorPayOrder(double amount, long orderDetailsId) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(key,key_secret);
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount*100); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_"+orderDetailsId);

            Order order = razorpayClient.orders.create(orderRequest);

            System.out.println(order);
            Optional<OrderDetails> optionalOrderDetails = orderDetailsRepository.findById(orderDetailsId);
            //code to save online payments;
            OrderPayments orderPayments = new OrderPayments();
            orderPayments.setAmount(amount);
            orderPayments.setOrderDetails(optionalOrderDetails.get());
            orderPayments.setOrderId(order.get("id"));
            orderPayments.setReceipt("order_rcptid_"+orderDetailsId);
            orderPayments.setStatus(order.get("status"));
            orderPaymentsRepository.save(orderPayments);
            return  order;
        } catch (RazorpayException e) {
            // Handle Exception
            System.out.println(e.getMessage());
            throw new RazorpayException("Couldn't create razor pay order");
        }
    }

    @Override
    public void updateOrderPayment(String paymentId, String orderId, String status) {
        OrderPayments orderPayments = orderPaymentsRepository.findByOrderId(orderId);
        if(orderPayments!=null){
            orderPayments.setPaymentId(paymentId);
            orderPayments.setStatus(status);
            orderPayments.setUpdateOn(new Date());
            orderPaymentsRepository.save(orderPayments);
        }
    }

    @Override
    public OrderPayments findByOrderDetails(long id) {
        return orderPaymentsRepository.findByOrderDetails(id);
    }

    @Override
    public double findPaidRevenue() {
        return orderPaymentsRepository.findPaidRevenue();
    }

    @Override
    public Double findMonthlyPaidRevenue() {
        return orderPaymentsRepository.findMonthlyPaidRevenue();
    }
}
