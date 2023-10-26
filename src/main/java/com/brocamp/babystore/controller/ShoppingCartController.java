package com.brocamp.babystore.controller;

import com.brocamp.babystore.model.Address;
import com.brocamp.babystore.model.PaymentMethods;
import com.brocamp.babystore.model.Product;
import com.brocamp.babystore.model.ShoppingCart;
import com.brocamp.babystore.repository.AddressRepository;
import com.brocamp.babystore.security.CustomUser;
import com.brocamp.babystore.service.AddressService;
import com.brocamp.babystore.service.PaymentMethodsService;
import com.brocamp.babystore.service.ProductService;
import com.brocamp.babystore.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class ShoppingCartController {
    private ShoppingCartService shoppingCartService;
    private ProductService productService;
    private PaymentMethodsService paymentMethodsService;
    private AddressService addressService;

    @PostMapping("/user_home/shoppingCart/add/{id}")
    public String addProductToCart(@PathVariable long id,
                                   Authentication authentication){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        long quantity = 1;
        if(customUser!=null){
            if(customUser.isBlocked()){
                return "redirect:/login";
            }else {
                Product product = productService.findById(id);
                if (quantity < 1) {
                    return "redirect:/user_home";

                } else if (product.getCurrentQuantity() < quantity) {
                    return "redirect:/user_home";
                } else {
                    if (productService.existsById(id)) {
                        shoppingCartService.addtoCart(customUser.getId(), id, quantity);
                        return "redirect:/user_home";
                    }
                }
            }
        }
        return "common/login";
    }
    @GetMapping("/user_home/shoppingCart")
    public String showCartItems(Authentication authentication, Model model){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser==null){
            return "redirect:/login";
        }else{
            if(customUser.isBlocked()){
                return "redirect:/login";
            }else {
                List<ShoppingCart> shoppingCartList = shoppingCartService.getSHoppingCartProductsByUsersId(customUser.getId());
                model.addAttribute("shoppingCartList", shoppingCartList);

                List<PaymentMethods> paymentMethodsList = paymentMethodsService.findAllPaymentMethods();
                model.addAttribute("paymentMethodsList", paymentMethodsList);

                List<Address> addressList = addressService.findAllByUsersId(customUser.getId());
                model.addAttribute("addressList", addressList);

                if (!shoppingCartList.isEmpty()) {
                    List<Object[]> obj = shoppingCartService.getQuantitySumAndTotalRateSum(customUser.getId());
                    for (Object[] ob : obj) {
                        long totalQuantity = (Long) ob[0];
                        Double cartTotal = (Double) ob[1];
                        model.addAttribute("totalQuantity", totalQuantity);
                        model.addAttribute("cartTotal", cartTotal);
                    }
                } else {
                    model.addAttribute("totalQuantity", 0);
                    model.addAttribute("cartTotal", 0);
                }
            }
            return "user/shop-cart";
        }

    }
    @GetMapping("/user_home/shoppingCart/remove/{id}")
    public String removeProductFromCart(@PathVariable long id,Authentication authentication){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser==null){
            return "redirect:/login";
        }else{
            if(customUser.isBlocked()){
                return "redirect:/login";
            }else {
                try {
                    shoppingCartService.removeProductFromCart(customUser.getId(), id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return "redirect:/user_home/shoppingCart";
    }
    @GetMapping("/user_home/shoppingCart/update/{id}")
    public String updateCart(@PathVariable long id,
                             @RequestParam("quantity") long quantity,
                             Authentication authentication,
                             HttpSession httpSession){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser==null){
            return "redirect:/login";
        }
        if(customUser.isBlocked()){
            return "redirect:/login";
        }
        if(quantity>0){
            Product product = productService.findById(id);
            if(quantity<product.getCurrentQuantity()){
                try {
                    shoppingCartService.updateCartQuantity(customUser.getId(), id,quantity);
                    httpSession.setAttribute("message","Cart Update");

                    long count = shoppingCartService.getTotalItemsInCartByUsersId(customUser.getId());
                    httpSession.setAttribute("cartItemCount",count);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }else{
                httpSession.setAttribute("message","Only "+product.getCurrentQuantity()+" Items available");
            }
        }else{
            httpSession.setAttribute("message","Please add items");

        }
        return "redirect:/user_home/shoppingCart?success";
    }
}
