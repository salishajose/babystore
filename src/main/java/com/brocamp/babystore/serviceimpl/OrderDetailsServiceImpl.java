package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.dto.OrderDetailsDTO;
import com.brocamp.babystore.dto.OrderdetailPaginationDto;
import com.brocamp.babystore.enums.TransactionType;
import com.brocamp.babystore.exception.OrderDetailsNotFoundException;
import com.brocamp.babystore.model.*;
import com.brocamp.babystore.repository.*;
import com.brocamp.babystore.service.CouponService;
import com.brocamp.babystore.service.OrderDetailsService;
import com.brocamp.babystore.service.WalletService;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@AllArgsConstructor
public class OrderDetailsServiceImpl implements OrderDetailsService {
    private OrderDetailsRepository orderDetailsRepository;
    private OrderProductsRepository orderProductsRepository;
    private AddressRepository addressRepository;
    private PaymentMethodsRepository paymentMethodsRepository;
    private ShoppingCartRepository shoppingCartRepository;
    private ProductRepository productRepository;
    private OrderPaymentsRepository orderPaymentsRepository;
    private CouponService couponService;
    private WalletService walletService;

    @Override
    public OrderDetails save(OrderDetailsDTO orderDetailsDTO, Users users) throws RazorpayException {
        //code to check whether any items are out of stock
        //long count = shoppingCartRepository.findOutofSto
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderDate(new Date());
        orderDetails.setOrderStatus("ORDERED");
        orderDetails.setUsers(users);
        Optional<Address> optionalAddress = addressRepository.findById(orderDetailsDTO.getAddressId());
        orderDetails.setAddress(optionalAddress.get());
        Optional<PaymentMethods> optionalPaymentMethods = paymentMethodsRepository.findById(orderDetailsDTO.getPaymentMethodsId());
        orderDetails.setPaymentMethods(optionalPaymentMethods.get());
        List<Object[]> obj  = shoppingCartRepository.getQuantitySumAndTotalRateSum(users.getId());

        long totalQuantity =0;
        Double cartTotal =0.0;
        for (Object[] ob : obj) {
            totalQuantity =(Long) ob[0];
            cartTotal =(Double) ob[1];
            orderDetails.setOrderAmount(cartTotal);
            orderDetails.setFinalAmount(cartTotal);
            orderDetails.setCouponId(orderDetailsDTO.getCouponId());
            orderDetails.setDiscount(0.0);
        }
        if(orderDetailsDTO.getCouponId()>0){
            Coupon coupon = couponService.findById(orderDetailsDTO.getCouponId());
            if(cartTotal>coupon.getMinimumOrderAmount()){
                //calculate
                double oldDiscount = cartTotal*(coupon.getDiscount()/100);
                String formattedDiscount = String.format("%.2f",oldDiscount);
                Double discount= Double.parseDouble(formattedDiscount);
                if(discount>coupon.getMaximumAmount()){
                    discount = coupon.getMaximumAmount();
                }
                double oldFinalAmount = cartTotal-discount;

                String formattedoldFinalAmount = String.format("%.2f",oldFinalAmount);
                Double finalAmount= Double.parseDouble(formattedoldFinalAmount);
                orderDetails.setDiscount(discount);
                orderDetails.setFinalAmount(finalAmount);
            }
        }
        orderDetailsRepository.save(orderDetails);
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAllByUsersId(users.getId());

        for (ShoppingCart cart: shoppingCartList){
            OrderProducts orderProducts = new OrderProducts();
            orderProducts.setProduct(cart.getProduct());
            orderProducts.setQuantity(cart.getQuantity());
            orderProducts.setIndividualRate(cart.getIndividualRate());
            orderProducts.setTotalRate(cart.getTotalRate());
            orderProducts.setOrderDetails(orderDetails);
            orderProductsRepository.save(orderProducts);
            Product product = cart.getProduct();
            long quantity = product.getCurrentQuantity()-cart.getQuantity();
            product.setCurrentQuantity(quantity);
            productRepository.save(product);
            cart.setDeleted(true);
            shoppingCartRepository.save(cart);
        }

        return orderDetails;
    }



    @Override
    public List<OrderDetails> findAllByUsersId(long usersId) {
        Optional<List<OrderDetails>> optionalOrderDetailsList = orderDetailsRepository.findByUsersId(usersId);
        return optionalOrderDetailsList.orElse(new ArrayList<OrderDetails>());
    }

    @Override
    public List<OrderDetailsDTO> findAllOrderDetailsProductsByUsersId(long usersId) {
        Optional<List<OrderDetails>> optionalOrderDetailsList = orderDetailsRepository.findByUsersId(usersId);
        List<OrderDetailsDTO> orderDetailsDTOList = new ArrayList<OrderDetailsDTO>();
        if(optionalOrderDetailsList.get()!=null){
            List<OrderDetails> orderDetailsList = optionalOrderDetailsList.get();
            for(OrderDetails orderDetails : orderDetailsList){
                OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
                orderDetailsDTO.setId(orderDetails.getId());
                orderDetailsDTO.setOrderDate(orderDetails.getOrderDate());
                orderDetailsDTO.setOrderAmount(orderDetails.getOrderAmount());
                orderDetailsDTO.setOrderStatus(orderDetails.getOrderStatus());
                orderDetailsDTO.setDeliveryDate(orderDetails.getDeliveryDate());
                orderDetailsDTO.setAddressId(orderDetails.getAddress().getId());
                orderDetailsDTO.setPaymentMethodsId(orderDetails.getPaymentMethods().getId());
                List<OrderProducts> orderProductsList = orderProductsRepository.findAllByOrderDetailsId(orderDetails.getId());
                orderDetailsDTO.setOrderProducts(orderProductsList);
                orderDetailsDTOList.add(orderDetailsDTO);
            }
            return orderDetailsDTOList;
        }else{
            return new ArrayList<OrderDetailsDTO>();
        }
    }

    @Override
    public boolean existsById(long id) {
        return orderDetailsRepository.existsById(id);
    }

    @Override
    public void cancelOrder(long orderDetailsId) {
        Optional<OrderDetails> optionalOrderDetails = orderDetailsRepository.findById(orderDetailsId);
        if(optionalOrderDetails.get()!=null){
            OrderDetails orderDetails = optionalOrderDetails.get();
            orderDetails.setOrderStatus("CANCELLED");
            List<OrderProducts> orderProductsList = orderProductsRepository.findAllByOrderDetailsId(orderDetailsId);
            for(OrderProducts orderProducts : orderProductsList){
                long quantity = orderProducts.getProduct().getCurrentQuantity();
                Product product = orderProducts.getProduct();
                long orderquantity= orderProducts.getQuantity();
                long newQuantity = quantity+orderquantity;
                product.setCurrentQuantity(newQuantity);
                productRepository.save(product);
            }
            orderDetailsRepository.save(orderDetails);
            if(orderDetails.getPaymentMethods().getPaymentMode().equalsIgnoreCase("razor pay")||
            orderDetails.getPaymentMethods().getPaymentMode().equalsIgnoreCase("wallet")){
                Wallet wallet = new Wallet();
                wallet.setUsersId(orderDetails.getUsers().getId());
                wallet.setAmount(orderDetails.getFinalAmount());
                wallet.setTransactionType(TransactionType.CREDITED);
                wallet.setUpdateOn(new Date());
                walletService.save(wallet);
            }

        }

    }

    @Override
    public void deliverOrder(long orderDetailsId) {
        Optional<OrderDetails> optionalOrderDetails = orderDetailsRepository.findById(orderDetailsId);
        if(optionalOrderDetails.get()!=null) {
            OrderDetails orderDetails = optionalOrderDetails.get();
            orderDetails.setOrderStatus("DELIVERED");
            orderDetails.setDeliveryDate(new Date());
            orderDetailsRepository.save(orderDetails);
        }
    }

    @Override
    public List<OrderDetailsDTO> findAllOrderDetails() {
        List<OrderDetailsDTO> orderDetailsDTOList = new ArrayList<OrderDetailsDTO>();
        Optional<List<OrderDetails>> optionalOrderDetailsList = Optional.ofNullable(orderDetailsRepository.findAll());
        if(!optionalOrderDetailsList.get().isEmpty()){
            List<OrderDetails> orderDetailsList = optionalOrderDetailsList.get();
            for(OrderDetails orderDetails : orderDetailsList){
                OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
                orderDetailsDTO.setId(orderDetails.getId());
                orderDetailsDTO.setOrderDate(orderDetails.getOrderDate());
                orderDetailsDTO.setOrderAmount(orderDetails.getOrderAmount());
                orderDetailsDTO.setOrderStatus(orderDetails.getOrderStatus());
                orderDetailsDTO.setDeliveryDate(orderDetails.getDeliveryDate());
                orderDetailsDTO.setAddressId(orderDetails.getAddress().getId());
                orderDetailsDTO.setPaymentMethodsId(orderDetails.getPaymentMethods().getId());
                orderDetailsDTO.setPaymentMethods(orderDetails.getPaymentMethods());
                orderDetailsDTO.setUsers(orderDetails.getUsers());
                List<OrderProducts> orderProductsList = orderProductsRepository.findAllByOrderDetailsId(orderDetails.getId());
                orderDetailsDTO.setOrderProducts(orderProductsList);
                orderDetailsDTOList.add(orderDetailsDTO);
            }
            return orderDetailsDTOList;
        }
        return orderDetailsDTOList;
    }

    @Override
    public OrderDetails findById(long id) {
        Optional<OrderDetails> optionalOrderDetails = orderDetailsRepository.findById(id);
        return optionalOrderDetails.orElseThrow(()->new OrderDetailsNotFoundException("Couldn't find order details of id : "+id));
    }

    @Override
    public OrderdetailPaginationDto findAllPaginatedOrderDetails(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        Page<OrderDetails> orderDetailsPage = orderDetailsRepository.findAll(pageable);
        OrderdetailPaginationDto orderdetailPaginationDto =new OrderdetailPaginationDto();
        orderdetailPaginationDto.setPageNo(pageNo);
        orderdetailPaginationDto.setTotalItems(orderDetailsPage.getTotalElements());
        List<OrderDetails> orderDetailsList = orderDetailsPage.getContent();
        orderdetailPaginationDto.setTotalPages(orderDetailsPage.getTotalPages());
        List<OrderDetailsDTO> orderDetailsDTOList = new ArrayList<>();
        for(OrderDetails orderDetails : orderDetailsList){
            OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
            orderDetailsDTO.setId(orderDetails.getId());
            orderDetailsDTO.setOrderDate(orderDetails.getOrderDate());
            orderDetailsDTO.setOrderAmount(orderDetails.getOrderAmount());
            orderDetailsDTO.setOrderStatus(orderDetails.getOrderStatus());
            orderDetailsDTO.setDeliveryDate(orderDetails.getDeliveryDate());
            orderDetailsDTO.setAddressId(orderDetails.getAddress().getId());
            orderDetailsDTO.setPaymentMethodsId(orderDetails.getPaymentMethods().getId());
            orderDetailsDTO.setPaymentMethods(orderDetails.getPaymentMethods());
            orderDetailsDTO.setUsers(orderDetails.getUsers());
            List<OrderProducts> orderProductsList = orderProductsRepository.findAllByOrderDetailsId(orderDetails.getId());
            orderDetailsDTO.setOrderProducts(orderProductsList);
            orderDetailsDTOList.add(orderDetailsDTO);
        }
        orderdetailPaginationDto.setOrderDetailsDTOList(orderDetailsDTOList);
        return orderdetailPaginationDto;
    }

    @Override
    public double findTotalDeliveredCODRevenue() {
        double deliveredCODAmount = orderDetailsRepository.findTotalDeliveredCODRevenue();
        return deliveredCODAmount;
    }

    @Override
    public long findTotalOrdersByOrderStatus(String orderStatus) {
        return orderDetailsRepository.findTotalOrdersByOrderStatus(orderStatus);
    }

    @Override
    public Double findMonthlyDeliveredCODRevenue() {
        return orderDetailsRepository.findMonthlyDeliveredCODRevenue();
    }

    @Override
    public long findAppliedCoupon(long couponId, long usersId) {
        return orderDetailsRepository.findAppliedCoupon(couponId,usersId);
    }

    @Override
    public List<Object[]> getMonthlySaleChartDetails() {
        return orderDetailsRepository.getMonthlySaleChartDetails();
    }

    @Override
    public void savePaymentMethods(OrderDetails orderDetails) {
        orderDetailsRepository.save(orderDetails);
    }

    @Override
    public void checkOnlineOrdersPayments(long usersId) {
        List<OrderPayments> orderPaymentsList = orderPaymentsRepository.findByOrderStatusAndPaymentStatus(usersId);
        System.out.println(orderPaymentsList);
        if(orderPaymentsList!=null){
            if(!orderPaymentsList.isEmpty()){
                for(OrderPayments orderPayments : orderPaymentsList){
                    OrderDetails orderDetails = orderPayments.getOrderDetails();
                    if(orderDetails.getOrderStatus().equalsIgnoreCase("ORDERED")){
                        orderDetails.setOrderStatus("CANCELLED");
                        orderDetailsRepository.save(orderDetails);
                    }
                }
            }
        }

    }


}
