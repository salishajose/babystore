package com.brocamp.babystore.controller;

import com.brocamp.babystore.dto.OrderProductsDTO;
import com.brocamp.babystore.exception.DocumentException;
import com.brocamp.babystore.service.OrderProductsService;
import com.brocamp.babystore.service.ProductService;
import com.brocamp.babystore.service.ReportGenerator;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

@Controller
@AllArgsConstructor
public class ReportsController {
    private OrderProductsService orderProductsService;
    private ProductService productService;
    private ReportGenerator reportGenerator;
    @GetMapping("/admin_panel/report/order")
    public String showOrderReports(Model model, HttpSession httpSession){
        List<OrderProductsDTO> orderProductsDTOList = orderProductsService.getAllProductSalesByDate("DELIVERED",new Date());
        model.addAttribute("orderProductsDTOList",orderProductsDTOList);
        httpSession.setAttribute("message","sales report of today");
        model.addAttribute("tittle","Mange Orders");
        model.addAttribute("size",orderProductsDTOList.size());
        return "admin/salesReport";
    }
    @PostMapping("/admin_panel/report/order/date")
    public String showOrderProductDatewise(@RequestParam("selectedDate")@DateTimeFormat(pattern = "yyyy-MM-dd") String selectedDate,
                                           Model model, HttpSession httpSession) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date=dateFormat.parse(selectedDate);
        List<OrderProductsDTO> orderProductsDTOList = orderProductsService.getAllProductSalesByDate("DELIVERED",date);
        model.addAttribute("orderProductsDTOList",orderProductsDTOList);
        httpSession.setAttribute("message","Sales report on : "+date);
        model.addAttribute("tittle","Mange Orders");
        model.addAttribute("size",orderProductsDTOList.size());
        return "admin/salesReport";
    }
    @PostMapping("/admin_panel/report/order/month")
    public String showOrderProductMonthwise(@RequestParam("month") String monthYear,
                                            Model model,
                                            HttpSession httpSession){

        String[] monthYearArray = monthYear.split("-");
        int year = Integer.parseInt(monthYearArray[0]);
        int month = Integer.parseInt(monthYearArray[1]);
        System.out.println("month = "+month+" year = "+year);
        List<OrderProductsDTO> orderProductsDTOList = orderProductsService.getAllProductSalesByMonthwise("DELIVERED",month,year);

        model.addAttribute("orderProductsDTOList",orderProductsDTOList);
        httpSession.setAttribute("message","Sales report of Month : "+monthYear);
        model.addAttribute("tittle","Mange Orders");
        model.addAttribute("size",orderProductsDTOList.size());
        return "admin/salesReport";

    }
    @PostMapping("/admin_panel/report/order/year")
    public String showOrderProductYearwise(@RequestParam("year") String year,
                                            Model model,
                                            HttpSession httpSession){
        int selectedYear = Integer.parseInt(year);
        List<OrderProductsDTO> orderProductsDTOList = orderProductsService.getAllProductSalesByYearwise("DELIVERED",selectedYear);

        model.addAttribute("orderProductsDTOList",orderProductsDTOList);
        httpSession.setAttribute("message","Sales report of Year : "+year);
        model.addAttribute("tittle","Mange Orders");
        model.addAttribute("size",orderProductsDTOList.size());
        return "admin/salesReport";
    }
    @PostMapping("/admin_panel/generateReport")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> salesReportGenerator(@RequestBody String requestData ) throws ParseException, IOException, DocumentException {

        JSONObject jsonObject = new JSONObject(requestData);

        String type = (String) jsonObject.get("type");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = dateFormat.parse((String) jsonObject.get("from"));
        Date toDate = dateFormat.parse((String) jsonObject.get("to"));

        String generatedFile="";

        List<Object[]> productStats = productService.getProductsStatsBetweenDates(fromDate,toDate);
        if(type.equals("csv")){
            generatedFile = reportGenerator.generateProductStatsCsv(productStats);
        }else {
            generatedFile = reportGenerator.generateProductStatsPdf(productStats, (String) jsonObject.get("from"), (String) jsonObject.get("to"));
        }


        File requestedFile = new File(generatedFile);
        ByteArrayResource resource = new ByteArrayResource(FileUtils.readFileToByteArray(requestedFile));
        HttpHeaders headers = new HttpHeaders();

        if(type.equals("csv")){
            headers.setContentType(MediaType.parseMediaType("text/csv"));
        }else{
            headers.setContentType(MediaType.APPLICATION_PDF);
        }
        headers.setContentDispositionFormData("attachment", generatedFile);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);

    }
    @GetMapping("/admin_panel/reports")
    public String getIndex(HttpSession session, Principal principal, Model model,
                           @RequestParam(name = "filter",required = false,defaultValue = "") String filter,
                           @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                           @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) {

        if (principal == null) {
            return "redirect:/login";
        } else {

            String period;


            switch (filter) {
                case "week" -> {
                    period="week";
                    // Get the starting date of the current week
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                    startDate = calendar.getTime();
                    // Get today's date
                    endDate = new Date();
                }
                case "month" -> {
                    period="month";
                    // Get the starting date of the current month
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    startDate = calendar.getTime();
                    // Get today's date
                    endDate = new Date();
                }
                case "day" -> {
                    period = "day";
                    // Get today's date
                    LocalDate today = LocalDate.now();
                    // Set the start date to 12:00:00 AM
                    LocalDateTime startDateTime = today.atStartOfDay();
                    // Set the end date to 11:59:59 PM
                    LocalDateTime endDateTime = today.atTime(23, 59, 59);

                    // Convert to Date objects
                    ZoneId zone = ZoneId.systemDefault();
                    startDate = Date.from(startDateTime.atZone(zone).toInstant());
                    endDate = Date.from(endDateTime.atZone(zone).toInstant());
                }

                case "year" -> {
                    period="year";
                    // Get the starting date of the current year
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_YEAR, 1);
                    startDate = calendar.getTime();
                    // Get today's date
                    endDate = new Date();
                }
                default -> {
                    // Default case: filter
                    period="";
                    filter="";

                }
            }
            if(filter!=""){
                List<Object[]>productStats = productService.getProductsStatsBetweenDates(startDate,endDate);
                model.addAttribute("productStats",productStats);
            }else{
                List<Object[]>productStats = productService.getProductStats();
                model.addAttribute("productStats",productStats);
            }
//            Double totalAmount = orderService.getTotalOrderAmount();
//            model.addAttribute("period", period);
//            Long totalProducts = productService.countAllProducts();
//            Long totalCategory = categoryService.countAllCategories();
//            Long totalOrders = orderService.countTotalConfirmedOrders();
//            Double monthlyRevenue = orderService.getTotalAmountForMonth();
//            model.addAttribute("TotalAmount",totalAmount);
//            model.addAttribute("TotalProducts",totalProducts);
//            model.addAttribute("TotalCategory",totalCategory);
//            model.addAttribute("TotalOrders",totalOrders);
//            model.addAttribute("MonthlyRevenue",monthlyRevenue);
//
//            session.setAttribute("userLoggedIn", true);
            return "admin/salesReportNew";
        }
    }
}
