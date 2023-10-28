package com.brocamp.babystore.controller;

import com.brocamp.babystore.dto.CategoryDTO;
import com.brocamp.babystore.model.Category;
import com.brocamp.babystore.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@Slf4j
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @PostMapping("/admin_panel/category/add")
    public String saveNewCategory(@Valid @ModelAttribute("category") CategoryDTO categoryDTO,
                                  BindingResult bindingResult,
                                  Model model,
                                  HttpSession httpSession) throws Exception {
        if(categoryDTO!=null){
            if(categoryService.existByName(categoryDTO.getName())){
                bindingResult.rejectValue("name",null,"Category already exists");
                httpSession.setAttribute("message","Can not create duplicate category");
                return "redirect:/admin_panel/category?error";
            }
            if(bindingResult.hasErrors()){
                model.addAttribute("category",categoryDTO);
                httpSession.setAttribute("message","Please enter alphabets only");
                return "redirect:/admin_panel/category?error";
            }else{
                Category category = new Category();
                category.setName(categoryDTO.getName());
                category.setCreatedAt(new Date());
                category.setUpdateOn(new Date());
                categoryService.saveOrUpdate(category);
                httpSession.setAttribute("message","Save succesfully");
                return "redirect:/admin_panel/category?success";
            }
        }else{
            model.addAttribute("category",categoryDTO);
            return "redirect:/admin_panel/category?success";
        }
    }

    @GetMapping("/admin_panel/category")
    public String getAllCategories(Model model) throws Exception {
        int pageSize = 10, pageNo = 1;
        Page<Category> categoryPage = categoryService.findAllCategoriesPaginated(pageNo, pageSize);
        List<Category> categoryList = categoryPage.getContent();
        System.out.println(categoryList);
        model.addAttribute("tittle", "Mange Category");
        model.addAttribute("categories", categoryList);
        model.addAttribute("size", categoryList.size());
        model.addAttribute("categoryNew", new Category());

        //for pagination
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",categoryPage.getTotalPages());
        model.addAttribute("totalItems",categoryPage.getTotalElements());

        return "admin/viewAllCategory";

    }
    @GetMapping("/admin_panel/category/page/{pageNo}")
    public String getAllCategoriesPaginated(@PathVariable int pageNo,
                                            Model model) throws Exception {
        int pageSize = 10;
        Page<Category> categoryPage = categoryService.findAllCategoriesPaginated(pageNo, pageSize);
        List<Category> categoryList = categoryPage.getContent();
        model.addAttribute("tittle", "Mange Category");
        model.addAttribute("categories", categoryList);
        model.addAttribute("size", categoryList.size());
        model.addAttribute("categoryNew", new Category());

        //for pagination
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",categoryPage.getTotalPages());
        model.addAttribute("totalItems",categoryPage.getTotalElements());

        return "admin/viewAllCategory";

    }
    @PostMapping("/admin_panel/category/update")
    public String updateCategory(Category category,BindingResult bindingResult) throws Exception {
        if(category!=null){
            if(bindingResult.hasErrors()){
                return "redirect:/admin_panel/category";
            }else{
                if(!categoryService.existByName(category.getName())){
                    Category categorynew = categoryService.findById(category.getId());
                    categorynew.setName(category.getName());
                    categorynew.setId(category.getId());
                    categorynew.setUpdateOn(new Date());
                    try{
                        categoryService.saveOrUpdate(categorynew);
                        return "redirect:/admin_panel/category";
                    }catch(Exception e){
                        e.printStackTrace();
                        throw new Exception("Internal server error:couldn't update category");
                    }
                }else{
                   // bindingResult.rejectValue("name","Already exists name");
                    return "redirect:/admin_panel/category";
                }

            }
        }else{
            //return "redirect:/admin_panel/category/update/"+id+"?error";
            return "redirect:/admin_panel/category";
        }
    }
    @GetMapping("/admin_panel/category/delete/{id}")
    public String deleteCategory(@PathVariable long id) throws Exception {
        if(categoryService.existsById(id)){
            //code to update is delete
            try{
                Category category =categoryService.findById(id);
                if(category!=null){
                    category.setDelete(true);
                    category.setUpdateOn(new Date());
                    try{
                        categoryService.saveOrUpdate(category);
                        return "redirect:/admin_panel/category";
                    }catch(Exception e){
                        log.info("Can not update category with id : "+id);
                        throw new Exception("Category Updation error");
                    }
                }else{
                    throw new Exception("NOT FOUND:category not found");
                }
            }catch(Exception e){
                log.info("Can not fetch category details with id : "+id);
                throw new Exception("Internal server error:Couldn't fetch Category");
            }
        }else{
            throw new Exception("Category not found");
        }
    }
    @GetMapping("/admin_panel/category/disable/{id}")
    public String disableCategory(@PathVariable long id){
        if(categoryService.existsById(id)){
            try {
                Category category = categoryService.findById(id);
                category.setBlocked(true);
                category.setUpdateOn(new Date());
                categoryService.saveOrUpdate(category);
            }catch (Exception e){
                log.info("Internal server error: "+ HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return "redirect:/admin_panel/category";
    }
    @GetMapping("/admin_panel/category/enable/{id}")
    public String enableCategory(@PathVariable long id){
        if(categoryService.existsById(id)){
            try {
                Category category = categoryService.findById(id);
                category.setBlocked(false);
                category.setUpdateOn(new Date());
                categoryService.saveOrUpdate(category);
            }catch (Exception e){
                log.info("Internal server error: "+ HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return "redirect:/admin_panel/category";
    }
    @GetMapping("/admin_panel/category/search")
    public String searchByName(@RequestParam("name") String name,
                               Model model){
        List<Category> categoryList = categoryService.findAllCategoriesByName(name);
        model.addAttribute("tittle", "Mange Category");
        model.addAttribute("categories", categoryList);
        model.addAttribute("size", categoryList.size());
        model.addAttribute("categoryNew", new Category());


        return "admin/viewAllCategory";
    }
}
