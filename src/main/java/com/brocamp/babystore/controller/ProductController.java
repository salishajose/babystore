package com.brocamp.babystore.controller;

import com.brocamp.babystore.model.Product;
import com.brocamp.babystore.model.ProductDto;
import com.brocamp.babystore.security.CustomUser;
import com.brocamp.babystore.service.CategoryService;
import com.brocamp.babystore.service.ImageUpload;
import com.brocamp.babystore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {
    private ProductService productService;
    private ImageUpload imageUpload;
    private CategoryService categoryService;

    public ProductController(ProductService productService, ImageUpload imageUpload, CategoryService categoryService) {
        this.productService = productService;
        this.imageUpload = imageUpload;
        this.categoryService = categoryService;
    }

    @GetMapping("/admin_panel/product")
    public String showAllProduct(Model model) throws Exception {
        try{
            int pageSize =10,pageNo=1;
            Page<Product> productPage = productService.findPaginated(pageNo,pageSize);
            List<Product> productList = productPage.getContent();
            //for pagination
            model.addAttribute("currentPage",pageNo);
            model.addAttribute("totalPages",productPage.getTotalPages());
            model.addAttribute("totalItems",productPage.getTotalElements());

            model.addAttribute("products",productList);
            model.addAttribute("size",productList.size());
            model.addAttribute("categories",categoryService.findAllCategories());

            return "admin/viewAllProducts";

        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Internal error: couldn't fetch data");
        }
    }
    @GetMapping("/admin_panel/product/page/{pageNo}")
    public String showAllPaginatedProduct(@PathVariable int pageNo,
                                          Model model) throws Exception {
        try{
            int pageSize =10;
            Page<Product> productPage = productService.findPaginated(pageNo,pageSize);
            List<Product> productList = productPage.getContent();
            //for pagination
            model.addAttribute("currentPage",pageNo);
            model.addAttribute("totalPages",productPage.getTotalPages());
            model.addAttribute("totalItems",productPage.getTotalElements());

            model.addAttribute("products",productList);
            model.addAttribute("size",productList.size());
            model.addAttribute("categories",categoryService.findAllCategories());

            return "admin/viewAllProducts";

        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Internal error: couldn't fetch data");
        }
    }
    @PostMapping("/admin_panel/product/add")
    public String saveProduct(@ModelAttribute("ProductDto") ProductDto productDto,
                              @RequestParam("imageProduct")List<MultipartFile> imageFiles,BindingResult bindingResult,
                              RedirectAttributes attributes,Model model) throws Exception {
        if(imageFiles.isEmpty()||productDto.getCategory()==null || bindingResult.hasErrors()){
            attributes.addFlashAttribute("duplicate","Please upload images and category");
            return "redirect:/admin_panel/product";
        }
        try {
            if (imageFiles != null && !imageFiles.isEmpty()) {
                List<String> uniqueFileNames = imageUpload.uploadImages(imageFiles);
                productDto.setImageUrls(uniqueFileNames);
            } else {
                productDto.setImageUrls(null);  // Handle the case when no images are uploaded
            }
             productService.save(productDto);
            attributes.addFlashAttribute("success","Add successfully");

        }catch (DataIntegrityViolationException e){
            attributes.addFlashAttribute("duplicate","Duplicate entry not possible");
        }
        catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to add");
        }
        return "redirect:/admin_panel/product";
    }
    @GetMapping(value="/admin_panel/product/enable-product/{id}")
    public String enabledProduct(@PathVariable("id") Long id, RedirectAttributes attributes){

        try {
            productService.enableById(id);
            attributes.addFlashAttribute("success", "Enabled successfully");
        }catch (Exception e){

            attributes.addFlashAttribute("error","Failed to enable");

        }

        return "redirect:/admin_panel/product";
    }
    @GetMapping(value="/admin_panel/product/disable-product/{id}")
    public String disabelProduct(@PathVariable("id") Long id, RedirectAttributes attributes){

        try {
            Product product = productService.findById(id);
            if(product!=null){
                product.setProductActivated(false);
                productService.update(product);
            }
            attributes.addFlashAttribute("success", "Enabled successfully");
        }catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to enable");

        }

        return "redirect:/admin_panel/product";
    }
    @GetMapping("/admin_panel/product/delete/{id}")
    public String deletedProduct(@PathVariable("id") Long id, RedirectAttributes attributes){

        try {
            Product product=productService.findById(id);
            if(product!=null){
                product.setProductDeleted(true);
                productService.update(product);
                attributes.addFlashAttribute("success", "Deleted successfully");
            }else{
                attributes.addFlashAttribute("error","No data found");
            }

        }catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to delete");

        }

        return "redirect:/admin_panel/product";
    }
    @GetMapping("/user_home/shop/category/{id}")
    public String showcategorywiseProducts(@PathVariable("id") Long id,
                                           Model model,
                                           Authentication authentication) throws Exception {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser!=null){
            if(customUser.isBlocked()){
                return "redirect:/login";
            }else{
                try{
                    List<Product> productList = productService.findAllByCategoryId(id);
                    model.addAttribute("categories",categoryService.findAllCategories());
                    model.addAttribute("products",productList);
                    return "user/user_home";
                }catch(Exception e){
                    e.printStackTrace();
                    throw new Exception("Internal error:No  data found");
                }
            }
        }else{
            return "redirect:/login";
        }

    }

    @GetMapping("/admin_panel/product/search")
    public String searchProduct(@RequestParam("name") String name, Model model){
        int pageSize =10,pageNo=1;
        //Page<Product> productPage = productService.findPaginatedNameContaining(name,pageNo,pageSize);
        List<Product> productList = productService.findProductNameContainig(name);
        model.addAttribute("products",productList);
        model.addAttribute("size",productList.size());
        return "admin/viewAllProducts";
    }
    @GetMapping("/user_home/product/search")
    public String usersearchProduct(@RequestParam("name") String name,
                                    Model model,Authentication authentication) throws Exception {

        if(name.trim().equals("")){
            return "user/user_home";
        }
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser!=null){
            if(customUser.isBlocked()){
                return "redirect:/login";
            }else{
                List<Product> productList = productService.findProductNameContainig(name);
                model.addAttribute("products",productList);
                model.addAttribute("categories",categoryService.findAllCategories());
                return "user/user_home";
            }
        }else{
            return "redirect:/login";
        }

    }
    @GetMapping("/index/product/search")
    public String indexSearchProduct(@RequestParam("name") String name, Model model) throws Exception {
        List<Product> productList = productService.findProductNameContainig(name);
        model.addAttribute("products",productList);
        model.addAttribute("categories",categoryService.findAllCategories());
        return "common/shop";
    }
    @GetMapping("/shop/product/search")
    public String shopSearchProduct(@RequestParam("name") String name, Model model) throws Exception {
        List<Product> productList = productService.findProductNameContainig(name);
        model.addAttribute("products",productList);
        model.addAttribute("categories",categoryService.findAllCategories());
        return "common/shop";
    }
    @GetMapping("/shop/category/{id}")
    public String showcategorywiseProductsInshop(@PathVariable("id") Long id,
                                           Model model) throws Exception {
        try{
            List<Product> productList = productService.findAllByCategoryId(id);
            model.addAttribute("categories",categoryService.findAllCategories());
            model.addAttribute("products",productList);
            return "common/shop";
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Internal error:No  data found");
        }

    }
    @GetMapping("/index/shop/category/{id}")
    public String showcategorywiseProductsInIndex(@PathVariable("id") Long id,
                                           Model model) throws Exception {
        try{
            List<Product> productList = productService.findAllByCategoryId(id);
            model.addAttribute("categories",categoryService.findAllCategories());
            model.addAttribute("products",productList);
            return "common/index";
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Internal error:No  data found");
        }

    }
    @PostMapping("/admin_panel/product/update")
    public String updateProduct(@ModelAttribute("product")Product product,
                                @RequestParam("imageProduct")List<MultipartFile> imageFiles) throws IOException {
        if(product!=null){
            if (imageFiles != null && !imageFiles.isEmpty()) {
                List<String> uniqueFileNames = imageUpload.uploadImages(imageFiles);
                if(uniqueFileNames.size()==0){
                    Product product1 = productService.findById(product.getId());
                    List<String> imageUrls = product1.getImageUrls();
                    product.setImageUrls(imageUrls);
                }else{
                    product.setImageUrls(uniqueFileNames);
                }

            }
            product.setSalePrice(product.getCostPrice());
            product.setProductActivated(true);
            product.setProductDeleted(false);
            productService.update(product);
        }
        return "redirect:/admin_panel/product";
    }
    @GetMapping("/user_home/product/page/{pageNo}")
    public String findPaginated(@PathVariable int pageNo,
                                Model model,
                                Authentication authentication){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        if(customUser!=null) {
            if (customUser.isBlocked()) {
                return "redirect:/login";
            }else{
                int pageSize =10;
                Page<Product> productPage = productService.findPaginated(pageNo,pageSize);
                List<Product> productList = productPage.getContent();
                model.addAttribute("currentPage",pageNo);
                model.addAttribute("totalPages",productPage.getTotalPages());
                model.addAttribute("totalItems",productPage.getTotalElements());
                model.addAttribute("products",productList);
                return "user/user_home";
            }
        }else{
            return "redirect:/login";
        }

    }
}
