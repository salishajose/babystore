package com.brocamp.babystore.controller;

import com.brocamp.babystore.model.*;
import com.brocamp.babystore.security.CustomUser;
import com.brocamp.babystore.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
@AllArgsConstructor
public class NavigationController {
    private CategoryService categoryService;
    private ProductService productService;
    private OrderDetailsService orderDetailsService;
    private OrderPaymentsService orderPaymentsService;
    private ShoppingCartService shoppingCartService;
    @GetMapping("/")
    public String showIndex(Model model) throws Exception {
        List<Product> productList =productService.findCurrentProducts();
        model.addAttribute("products",productList);
        model.addAttribute("categories",categoryService.findAllCategories());

        return "common/index";
    }
    @GetMapping("/shop")
    public String showshop(Model model) throws Exception {
        List<Product> productList =productService.findCurrentProducts();
        model.addAttribute("products",productList);
        model.addAttribute("categories",categoryService.findAllCategories());

        return "common/shop";
    }

    @GetMapping("/login")
    public String showLoginPage(){
        return "common/login";

    }
//@GetMapping("/login")
//public String showLoginPage(){
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    if(authentication==null || authentication instanceof AnonymousAuthenticationToken){
//        return "common/login";
//    }else{
//        return "redirect:/";
//    }
//
//}

    @GetMapping("/signup")
    public  String showSignUp(Model model){
        String email = (String) model.asMap().get("email");
        Users users = new Users();
        users.setEmail(email);
        model.addAttribute("newUsers",users);
        return "common/signup";
    }
    @GetMapping("/verifyEmail")
    public String showVerifyEmail(){
        return "common/verifyEmail";
    }
    @GetMapping("/otpvalidation")
    public String showotpvalidationPage(Model model, HttpSession session){
        String email = (String) model.asMap().get("email");
        UserOTP userOTP = new UserOTP();
        userOTP.setEmail(email);
        session.setAttribute("email",email);
        model.addAttribute("userOTP",userOTP);
        return "common/otpvalidation";
    }
    @GetMapping("/forgotpassword")
    public String showForgotpasswordPage(){
        return "common/forgotpassword";
    }

    @GetMapping("/forgotPasswordOTPLogin")
    public String showForgotPasswordOTPLogin(Model model,HttpSession session){
        String email = (String) model.asMap().get("email");
        UserOTP userOTP = new UserOTP();
        userOTP.setEmail(email);
        session.setAttribute("email",email);
        model.addAttribute("userOTP",userOTP);
        return "common/forgotPasswordOTPLogin";
    }
    @GetMapping("/admin_panel")
    public String showAdminPanel(Model model, ModelMap modelMap){
        // code to get total revenue
        double codRevenue = orderDetailsService.findTotalDeliveredCODRevenue();
        double onlineRevenue = orderPaymentsService.findPaidRevenue();
        double totalRevenue = codRevenue+onlineRevenue;
        model.addAttribute("codRevenue",codRevenue);
        model.addAttribute("onlineRevenue",onlineRevenue);
        model.addAttribute("totalRevenue",totalRevenue);

        // code to get total order
        long cancelledOrder = orderDetailsService.findTotalOrdersByOrderStatus("CANCELLED");
        long pendigOrder = orderDetailsService.findTotalOrdersByOrderStatus("ORDERED");
        long deliveredOrder = orderDetailsService.findTotalOrdersByOrderStatus("DELIVERED");
        long totalOrders=cancelledOrder+pendigOrder+deliveredOrder;

        model.addAttribute("cancelledOrder",cancelledOrder);
        model.addAttribute("pendigOrder",pendigOrder);
        model.addAttribute("deliveredOrder",deliveredOrder);
        model.addAttribute("totalOrders",totalOrders);

        double orderedPercent = (double)(pendigOrder*100/totalOrders);
        double deliveredPercent = (double)(deliveredOrder*100/totalOrders);
        double cancelledPercent = (double)(cancelledOrder*100/totalOrders);

        model.addAttribute("orderedPercent",orderedPercent);
        model.addAttribute("deliveredPercent",deliveredPercent);
        model.addAttribute("cancelledPercent",cancelledPercent);
        //code to fetch products count
        long totalProducts = productService.findTotalNotDeletedProducts();
        long outOfStockProducts = productService.findOutOfStockProducts();
        long activeProducts = productService.findActiveProducts();
        long inactiveProducts = productService.findInActiveProducts();


        model.addAttribute("totalProducts",totalProducts);
        model.addAttribute("outOfStockProducts",outOfStockProducts);
        model.addAttribute("activeProducts",activeProducts);
        model.addAttribute("inactiveProducts",inactiveProducts);

        //code to fetch category count
        long totalCategories = categoryService.findTotalCategoryCount();
        long blockedCategories = categoryService.findBlockedCategoryCount();
        long unblockedCategories = categoryService.findUnblockedCategoryCount();

        model.addAttribute("totalCategories",totalCategories);
        model.addAttribute("blockedCategories",blockedCategories);
        model.addAttribute("unblockedCategories",unblockedCategories);
        //code to fetch monthly revenue

        Double monthlyCodRevenue = orderDetailsService.findMonthlyDeliveredCODRevenue();
        Double monthlyOnlineRevenue = orderPaymentsService.findMonthlyPaidRevenue();
        Double monthlyRevenue = monthlyCodRevenue+onlineRevenue;
        model.addAttribute("monthlyCodRevenue",monthlyCodRevenue);
        model.addAttribute("monthlyOnlineRevenue",monthlyOnlineRevenue);
        model.addAttribute("monthlyRevenue",monthlyRevenue);

        List<Object[]> objList = orderDetailsService.getMonthlySaleChartDetails();
//
//        List<String> nameList= productService.findAll().stream().map(x->x.getName()).collect(Collectors.toList());
//        List<Long> ageList = productService.findAll().stream().map(x-> x.getCurrentQuantity()).collect(Collectors.toList());

        List<String> datelist = objList.stream()
                .map(obj -> (String) obj[0])
                .collect(Collectors.toList());

        List<Double> amountlist = objList.stream()
                .map(obj -> (Double) obj[1])
                .collect(Collectors.toList());

        model.addAttribute("month", datelist);
        model.addAttribute("sale", amountlist);
        return "admin/admin_panel";
    }

    @GetMapping("/user_home")
    public String showUserHomePage(Model model,
                                   Authentication authentication,
                                   HttpSession httpSession) throws Exception {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser!=null){
            if(customUser.isBlocked()){
                return "redirect:/login";
            }else{
                int pageSize =4,pageNo=1;
                Page<Product> productPage = productService.findPaginated(pageNo,pageSize);
                List<Product> productList = productPage.getContent();
                model.addAttribute("currentPage",pageNo);
                model.addAttribute("totalPages",productPage.getTotalPages());
                model.addAttribute("totalItems",productPage.getTotalElements());
                model.addAttribute("products",productList);
                model.addAttribute("categories",categoryService.findAllCategories());
                //code to find total items in cart
                long count = shoppingCartService.getTotalItemsInCartByUsersId(customUser.getId());
                httpSession.setAttribute("cartItemCount",count);
                return "user/user_home";
            }
        }else{
            return "redirect:/login";
        }

    }



    @GetMapping("/admin_panel/category/update/{id}")
    @ResponseBody
    public Optional<Category> showUpdateUser(@PathVariable long id, Model model) throws Exception {
        try{
            if(categoryService.existsById(id)){
                try{
                    Optional<Category> optionalCategory = Optional.ofNullable(categoryService.findById(id));
                    return optionalCategory;
                }catch(Exception e){
                    e.printStackTrace();
                    throw new Exception("Internal server error:can not fetch category details...");
                }
            }else{
                throw new Exception("Category not found");
            }

        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Can not find category.Internal server error");
        }
    }

    @GetMapping("/admin_panel/sizeChart/add")
    public String showNewSizechart(Model model){
        model.addAttribute("sizeChart",new SizeChart());
        return "admin/sizechartAdd";
    }
    @GetMapping("/admin_panel/product/add")
    public String showNewProduct(Model model) throws Exception {
        model.addAttribute("title","Add Product");
        model.addAttribute("categories",categoryService.findAllCategories());
        model.addAttribute("productDto",new ProductDto());
        return "admin/productsAdd";
    }
    @GetMapping("/user_home/shop/viewproduct/{id}")
    public String viewProductDetails(@PathVariable("id")long id,Model model,Authentication authentication) throws Exception {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser!=null) {
            if (customUser.isBlocked()) {
                return "redirect:/login";
            }else{
                Product product= productService.findById(id);
                if(product!=null){
                    model.addAttribute("product",product);
                    model.addAttribute("title","Product Detail");
                    model.addAttribute("page","Product Detail");
                    model.addAttribute("productDetail",product);
                    model.addAttribute("categories",categoryService.findAllCategories());
                }else{
                    return "redirect:/user_home";
                }
                return "user/viewProduct";
            }
        }else{
            return "redirect:/login";
        }
    }
    @GetMapping("/admin_panel/product/update/{id}")
    public String showUpdateProduct(@PathVariable long id,Model model) throws Exception {
        Product product = productService.findById(id);
        if(product!=null){
            model.addAttribute("title","Update Product");
            model.addAttribute("categories",categoryService.findAllCategories());
            model.addAttribute("product",product);
            return "admin/updateProduct";
        }else{
            return "redirect:/admin_panel/product";
        }
    }

    @GetMapping("/barChart")
    public String getAllEmployee(Model model) {

        List<String> nameList= productService.findAll().stream().map(x->x.getName()).collect(Collectors.toList());
        List<Long> ageList = productService.findAll().stream().map(x-> x.getCurrentQuantity()).collect(Collectors.toList());
        model.addAttribute("name", nameList);
        model.addAttribute("age", ageList);
        return "admin/barChart";

    }
}
