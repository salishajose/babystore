package com.brocamp.babystore.controller;

import com.brocamp.babystore.model.SizeChart;
import com.brocamp.babystore.service.SizeChartService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class SizeChartController {
    private SizeChartService sizeChartService;
    public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/sizeImages";


    public SizeChartController(SizeChartService sizeChartService) {
        this.sizeChartService = sizeChartService;
    }
    @PostMapping("admin_panel/sizeChart/add")
    public String saveNewSizechart(@Valid @ModelAttribute("sizeChart") SizeChart sizeChart,
                                   @RequestParam("productImage")MultipartFile file,
                                   @RequestParam("imgName")String imgName,
                                   BindingResult bindingResult) throws Exception {
        if(!bindingResult.hasErrors()){
            String imageUUID;
            if(!file.isEmpty()){
                imageUUID = file.getOriginalFilename();
                Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
                Files.write(fileNameAndPath,file.getBytes());
            }else{
                imageUUID = imgName;
            }
            sizeChart.setImage(imageUUID);
            try{
                sizeChartService.saveOrUpdate(sizeChart);
                return "redirect:/admin_panel/sizeChart/add";
            }catch(Exception e){
                e.printStackTrace();
                throw new Exception("Internal server error:Couldn't save size chart");
            }
        }else{
            return "redirect:/admin_panel/sizeChart/add";
        }

    }
    @GetMapping("/admin_panel/sizeChart")
    public String viewAllSizechart(Model model) throws Exception {
        try{
            List<SizeChart> sizeChartList = sizeChartService.findAll();
            model.addAttribute("sizeChartList",sizeChartList);
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Internal server error:Couldn't fetch sizechart details");
        }
        return "admin/viewAllSizeChart";
    }
}
