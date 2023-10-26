package com.brocamp.babystore.controller;

import com.brocamp.babystore.dto.OrderDetailsDTO;
import com.brocamp.babystore.dto.OrderdetailPaginationDto;
import com.brocamp.babystore.enums.TransactionType;
import com.brocamp.babystore.model.*;
import com.brocamp.babystore.repository.WalletRepository;
import com.brocamp.babystore.security.CustomUser;
import com.brocamp.babystore.service.*;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class OrderDetailsController {
    private OrderDetailsService orderDetailsService;
    private UsersSevice usersSevice;
    private AddressService addressService;
    private PaymentMethodsService paymentMethodsService;
    private OrderProductsService orderProductsService;
    private OrderPaymentsService orderPaymentsService;
    private ShoppingCartService shoppingCartService;
    private WalletService walletService;

    @PostMapping("/user_home/order/add")
    public String saveOrderDetails(@ModelAttribute("orderDetailsDTO") OrderDetailsDTO orderDetailsDTO,
                                   Authentication authentication,
                                   BindingResult bindingResult, Model model,
                                   HttpSession httpSession) throws Exception {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser==null){
            return "redirect:/login";
        }
        if(customUser.isBlocked()){
            return "redirect:/login";
        }
        if(orderDetailsDTO.getAddressId()==0 || orderDetailsDTO.getPaymentMethodsId()==0){
            List<Address> addressList = addressService.findAllByUsersId(customUser.getId());
            List<PaymentMethods> paymentMethodsList = paymentMethodsService.findAllPaymentMethods();
            model.addAttribute("paymentMethodsList",paymentMethodsList);
            model.addAttribute("addressList", addressList);
            model.addAttribute("orderDetailsDTO",orderDetailsDTO);
            httpSession.setAttribute("message","Please choose address and payment method");
            return "redirect:/user_home/address?error";
        }else{
            if(customUser == null){
                return "common/login";
            }else{
                Users users = usersSevice.findById(customUser.getId());
                try{
                    OrderDetails orderDetails = orderDetailsService.save(orderDetailsDTO,users);
                    PaymentMethods paymentMethods = paymentMethodsService.findById(orderDetailsDTO.getPaymentMethodsId());

                    long count = shoppingCartService.getTotalItemsInCartByUsersId(customUser.getId());
                    httpSession.setAttribute("cartItemCount",count);
                    if(paymentMethods!=null){
                        if(paymentMethods.getPaymentMode().equalsIgnoreCase("razor pay")){
                            model.addAttribute("orderDetails",orderDetails);
                            return "user/paymentPage";
                        }else if(paymentMethods.getPaymentMode().equalsIgnoreCase("wallet")){
                            double amountInWallet = walletService.findSumOfWalletAmount(customUser.getId());
                            if(orderDetails.getFinalAmount()<=amountInWallet){
                                //change payment method type 3 (wallet)
                                //insert debited entry in wallet
                                Wallet wallet = new Wallet();
                                wallet.setUsersId(orderDetails.getUsers().getId());
                                wallet.setAmount(orderDetails.getFinalAmount());
                                wallet.setTransactionType(TransactionType.DEBITED);
                                wallet.setUpdateOn(new Date());
                                walletService.save(wallet);
                                httpSession.setAttribute("message","Order placed Successfully");
                            }else{
                                //paument method cash on delivery is updatinng instead of wallet
                                PaymentMethods paymentMethods1 = paymentMethodsService.findById(1);
                                orderDetails.setPaymentMethods(paymentMethods1);
                                orderDetailsService.savePaymentMethods(orderDetails);
                                httpSession.setAttribute("message","Oder placed with COD.There is No suffiient wallet balance");
                            }
                            return "redirect:/user_home/order?success";
                        }else{
                            httpSession.setAttribute("message","Order placed Successfully");
                            return "redirect:/user_home/order?success";
                        }
                    }else{
                        httpSession.setAttribute("message","Order placed Successfully");
                        return "redirect:/user_home/order?success";
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    throw new Exception("Interrnal server error");
                }
            }

        }
    }
    @GetMapping("/user_home/order")
    public String showOrders(Model model,Authentication authentication,HttpSession httpSession){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser!=null){
            if(customUser.isBlocked()){
                return "redirect:/login";
            }else {
                //List<OrderProducts> orderProductsList = orderProductsService.findAllByUsersId(customUser.getId());
                //List<OrderDetailsDTO> orderDetailsDTOList = orderDetailsService.findAllOrderDetailsProductsByUsersId(customUser.getId());
                int pageSize =3,pageNo=1;
                Page<OrderProducts> orderProductsPage = orderProductsService.findAllByUsersIdPaginated(
                        customUser.getId(),pageNo,pageSize);

                List<OrderProducts> orderProductsList = orderProductsPage.getContent();
                model.addAttribute("currentPage",pageNo);
                model.addAttribute("totalPages",orderProductsPage.getTotalPages());
                model.addAttribute("totalItems",orderProductsPage.getTotalElements());

                model.addAttribute("orderProductsList",orderProductsList);
                return "user/myOrders";
            }
        }else{
            return "common/login";
        }
    }
    @GetMapping("/user_home/order/page/{pageNo}")
    public String showOrders(@PathVariable int pageNo,
                             Model model,
                             Authentication authentication){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser!=null){
            if(customUser.isBlocked()){
                return "redirect:/login";
            }else {
                List<OrderProducts> orderProductsList = orderProductsService.findAllByUsersId(customUser.getId());
                //List<OrderDetailsDTO> orderDetailsDTOList = orderDetailsService.findAllOrderDetailsProductsByUsersId(customUser.getId());
                int pageSize =3;
                Page<OrderProducts> orderProductsPage = orderProductsService.findAllByUsersIdPaginated(
                        customUser.getId(),pageNo,pageSize);


                model.addAttribute("currentPage",pageNo);
                model.addAttribute("totalPages",orderProductsPage.getTotalPages());
                model.addAttribute("totalItems",orderProductsPage.getTotalElements());

                model.addAttribute("orderProductsList", orderProductsPage.getContent());
                return "user/myOrders";
            }
        }else{
            return "common/login";
        }
    }
    @GetMapping("/admin_panel/order")
    public String showAdminOrders(Model model,Authentication authentication){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser!=null){
            // List<OrderProducts> orderProductsList = orderProductsService.findAllByUsersId(customUser.getId());
            int pageSize =5,pageNo=1;
            OrderdetailPaginationDto orderdetailPaginationDto = orderDetailsService.findAllPaginatedOrderDetails(pageNo,pageSize);
            List<OrderDetailsDTO> orderDetailsDTOList = orderdetailPaginationDto.getOrderDetailsDTOList();

            model.addAttribute("currentPage",orderdetailPaginationDto.getPageNo());
            model.addAttribute("totalPages",orderdetailPaginationDto.getTotalPages());
            model.addAttribute("totalItems",orderdetailPaginationDto.getTotalItems());

            model.addAttribute("orderDetailsDTOList",orderDetailsDTOList);
            model.addAttribute("tittle","Mange Orders");
            model.addAttribute("size",orderDetailsDTOList.size());
            model.addAttribute("orderProductsList",new ArrayList<OrderProducts>());
            return "admin/viewAllOrders";
        }else{
            return "common/login";
        }
    }

    @GetMapping("/admin_panel/order/page/{pageNo}")
    public String showAdminOrdersPaginated(@PathVariable int pageNo,
                                           Model model,
                                           Authentication authentication){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser!=null){
            // List<OrderProducts> orderProductsList = orderProductsService.findAllByUsersId(customUser.getId());
            int pageSize =5;
            OrderdetailPaginationDto orderdetailPaginationDto = orderDetailsService.findAllPaginatedOrderDetails(pageNo,pageSize);
            List<OrderDetailsDTO> orderDetailsDTOList = orderdetailPaginationDto.getOrderDetailsDTOList();

            model.addAttribute("currentPage",orderdetailPaginationDto.getPageNo());
            model.addAttribute("totalPages",orderdetailPaginationDto.getTotalPages());
            model.addAttribute("totalItems",orderdetailPaginationDto.getTotalItems());

            model.addAttribute("orderDetailsDTOList",orderDetailsDTOList);
            model.addAttribute("tittle","Mange Orders");
            model.addAttribute("size",orderDetailsDTOList.size());
            model.addAttribute("orderProductsList",new ArrayList<OrderProducts>());
            return "admin/viewAllOrders";
        }else{
            return "common/login";
        }
    }

    @GetMapping("/user_home/order/cancel/{id}")
    public String cancelOrder(@PathVariable long id,Authentication authentication){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser!=null){
            if(customUser.isBlocked()){
                return "redirect:/login";
            }else{
                if(orderDetailsService.existsById(id)){
                    orderDetailsService.cancelOrder(id);
                }
                return "redirect:/user_home/order";
            }
        }else{
            return "redirect:/login";
        }
    }
    @GetMapping("/admin_panel/order/cancel/{id}")
    public String adminCancelOrder(@PathVariable long id){
        if(orderDetailsService.existsById(id)){
            orderDetailsService.cancelOrder(id);
        }
        return "redirect:/admin_panel/order";
    }
    @GetMapping("/admin_panel/order/deliver/{id}")
    public String deliverOrder(@PathVariable long id){
        if(orderDetailsService.existsById(id)){
            orderDetailsService.deliverOrder(id);
        }
        return "redirect:/admin_panel/order";
    }
    @GetMapping("/admin_panel/order/product/{id}")
    public String showOrderProducts(@PathVariable long id,
                                                 Model model){
        OrderDetails orderDetails = orderDetailsService.findById(id);
        model.addAttribute("orderDetails",orderDetails);
        model.addAttribute("orderProductsList",orderProductsService.findByOrderDetailsId(id));
        OrderPayments orderPayments = new OrderPayments();
        if(orderDetails.getPaymentMethods().getPaymentMode().equalsIgnoreCase("razor pay")){
            orderPayments= orderPaymentsService.findByOrderDetails(orderDetails.getId());
        }
        model.addAttribute("orderPayments",orderPayments);
        return "admin/order-view";
    }
    @PostMapping("/user_home/createOrder")
    @ResponseBody
    public String createOrderRazor(@RequestBody Map<String,Object> data) throws RazorpayException {
        System.out.println("order function called");
        // code to create razor pay order
        double amount = Double.parseDouble((data.get("amount").toString()));
        long orderDetails_id = Long.parseLong(data.get("orderDetails_id").toString());

        Order order = orderPaymentsService.createRazorPayOrder(amount,orderDetails_id);

        return order.toString();
    }
    @PostMapping("/user_home/updateRazorpayOrder")
    @ResponseBody
    public ResponseEntity<?> updateRazorpayOrder(@RequestBody Map<String,Object> data) throws RazorpayException {
        System.out.println("order updation  function called");
        // code to create razor pay order
        String payment_id = data.get("payment_id").toString();
        String order_id = data.get("order_id").toString();
        String status = data.get("status").toString();
        orderPaymentsService.updateOrderPayment(payment_id,order_id,status);
        return ResponseEntity.ok(Map.of("msg","updated"));
    }
    @GetMapping("/user_home/payment")
    public String showPayment(){
        return "user/paymentPage";
    }
}
