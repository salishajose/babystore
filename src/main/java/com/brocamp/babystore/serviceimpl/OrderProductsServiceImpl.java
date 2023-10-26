package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.dto.OrderProductsDTO;
import com.brocamp.babystore.exception.OrderProductsNotFoundException;
import com.brocamp.babystore.exception.ProductNotFoundException;
import com.brocamp.babystore.model.OrderProducts;
import com.brocamp.babystore.model.Product;
import com.brocamp.babystore.repository.OrderProductsRepository;
import com.brocamp.babystore.repository.ProductRepository;
import com.brocamp.babystore.service.OrderProductsService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class OrderProductsServiceImpl implements OrderProductsService {
    private OrderProductsRepository orderProductsRepository;
    private ProductRepository productRepository;
    @Override
    public List<OrderProducts> findAllByUsersId(long usersId) {
        Optional<List<OrderProducts>> optionalOrderProductsList = orderProductsRepository.findAllByUsersId(usersId);
        return optionalOrderProductsList.orElse(new ArrayList<OrderProducts>());
    }

    @Override
    public List<OrderProducts> findByOrderDetailsId(long id) {
        Optional<List<OrderProducts>> optionalOrderProductsList = orderProductsRepository.findByOrderDetailsId(id);
        return optionalOrderProductsList.orElseThrow(()->new OrderProductsNotFoundException("Order Products not found for order id : "+id));
    }

    @Override
    public Page<OrderProducts> findAllByUsersIdPaginated(long userId,int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        return orderProductsRepository.findAllByUsersIdPaginated(userId,pageable);
    }

    @Override
    public List<OrderProductsDTO> getAllProductSalesByDate(String status, Date date) {
        List<Object[]> orderProductsObj =orderProductsRepository.getAllProductSalesByDate(status,date);
        List<OrderProductsDTO> orderProductsDTOList = new ArrayList<>();
        if(!orderProductsObj.isEmpty()){
            for (Object[] ob : orderProductsObj) {
                OrderProductsDTO orderProductsDTO = new OrderProductsDTO();
                Long productId = (Long) ob[0];
                Long totalQuantity = (Long) ob[1];
                Double totalAmount = (Double) ob[2];
                Product product = productRepository.findById(productId).orElseThrow(()-> new ProductNotFoundException("Poduct not found with id:"+productId));
                orderProductsDTO.setProduct(product);
                orderProductsDTO.setQuantity(totalQuantity);
                orderProductsDTO.setTotalRate(totalAmount);
                orderProductsDTOList.add(orderProductsDTO);
            }
        }

        return orderProductsDTOList;
    }

    @Override
    public List<OrderProductsDTO> getAllProductSalesByMonthwise(String status, int month, int year) {
        {
            List<Object[]> orderProductsObj =orderProductsRepository.getAllProductSalesByMonthwise(status,month,year);
            List<OrderProductsDTO> orderProductsDTOList = new ArrayList<>();
            if(!orderProductsObj.isEmpty()){
                for (Object[] ob : orderProductsObj) {
                    OrderProductsDTO orderProductsDTO = new OrderProductsDTO();
                    Long productId = (Long) ob[0];
                    Long totalQuantity = (Long) ob[1];
                    Double totalAmount = (Double) ob[2];
                    Product product = productRepository.findById(productId).orElseThrow(()-> new ProductNotFoundException("Poduct not found with id:"+productId));
                    orderProductsDTO.setProduct(product);
                    orderProductsDTO.setQuantity(totalQuantity);
                    orderProductsDTO.setTotalRate(totalAmount);
                    orderProductsDTOList.add(orderProductsDTO);
                }
            }

            return orderProductsDTOList;
        }
    }

    @Override
    public List<OrderProductsDTO> getAllProductSalesByYearwise(String status, int selectedYear) {
        {
            List<Object[]> orderProductsObj =orderProductsRepository.getAllProductSalesByYearwise(status,selectedYear);
            List<OrderProductsDTO> orderProductsDTOList = new ArrayList<>();
            if(!orderProductsObj.isEmpty()){
                for (Object[] ob : orderProductsObj) {
                    OrderProductsDTO orderProductsDTO = new OrderProductsDTO();
                    Long productId = (Long) ob[0];
                    Long totalQuantity = (Long) ob[1];
                    Double totalAmount = (Double) ob[2];
                    Product product = productRepository.findById(productId).orElseThrow(()-> new ProductNotFoundException("Poduct not found with id:"+productId));
                    orderProductsDTO.setProduct(product);
                    orderProductsDTO.setQuantity(totalQuantity);
                    orderProductsDTO.setTotalRate(totalAmount);
                    orderProductsDTOList.add(orderProductsDTO);
                }
            }

            return orderProductsDTOList;
        }
    }


}
