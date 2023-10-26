package com.brocamp.babystore.service;

import com.brocamp.babystore.dto.OrderDetailsDTO;
import com.brocamp.babystore.dto.OrderdetailPaginationDto;
import com.brocamp.babystore.model.OrderDetails;
import com.brocamp.babystore.model.Users;
import com.razorpay.RazorpayException;

import java.util.List;

public interface OrderDetailsService {
    OrderDetails save(OrderDetailsDTO orderDetailsDTO, Users users) throws RazorpayException;


    List<OrderDetails> findAllByUsersId(long usersId);

    List<OrderDetailsDTO> findAllOrderDetailsProductsByUsersId(long id);

    boolean existsById(long id);

    void cancelOrder(long orderDetailsId);

    void deliverOrder(long id);

    List<OrderDetailsDTO> findAllOrderDetails();

    OrderDetails findById(long id);

    OrderdetailPaginationDto findAllPaginatedOrderDetails(int pageNo, int pageSize);


    double findTotalDeliveredCODRevenue();


    long findTotalOrdersByOrderStatus(String orderStatus);

    Double findMonthlyDeliveredCODRevenue();

    long findAppliedCoupon(long couponId, long usersId);

    List<Object[]> getMonthlySaleChartDetails();

    void savePaymentMethods(OrderDetails orderDetails);
}
