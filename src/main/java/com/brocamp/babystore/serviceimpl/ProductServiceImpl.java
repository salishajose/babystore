package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.exception.ProductNotFoundException;
import com.brocamp.babystore.model.Product;
import com.brocamp.babystore.model.ProductDto;
import com.brocamp.babystore.repository.ProductRepository;
import com.brocamp.babystore.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public void save(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCostPrice(productDto.getCostPrice());
        //initially costprice and saleprice are same
        product.setSalePrice(productDto.getCostPrice());
        product.setCurrentQuantity(productDto.getCurrentQuantity());
        product.setImageUrls(productDto.getImageUrls());
        product.setCategory(productDto.getCategory());
        product.setBrand(null);
        product.setProductActivated(true);
        product.setProductDeleted(false);
        productRepository.save(product);
    }


    @Override
    public void enableById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.get()!=null){
            Product product=optionalProduct.get();
            optionalProduct.get().setProductActivated(true);
            productRepository.save(product);
        }
    }

    @Override
    public Product findById(Long id) {
        Optional<Product> optionalProduct =productRepository.findById(id);
        return optionalProduct.orElseThrow(()-> new ProductNotFoundException("Can not find product with id : "+id));
    }

    @Override
    public List<Product> findAllProducteNotDeleted() {
        return productRepository.findAllByProductDeletedFalse();
    }

    @Override
    public List<Product> findAllByCategoryId(Long id) {
        return productRepository.findAllByCategoryId(id);
    }

    @Override
    public List<Product> findProductNameContainig(String name) {
        Optional<List<Product>> optionalProducts =Optional.ofNullable(productRepository.findAllByNameContainingIgnoreCase(name));
        return optionalProducts.orElse(new ArrayList<Product>());
    }

    @Override
    public List<Product> findCurrentProducts() {
        Optional<List<Product>> optionalProducts= productRepository.findCurrentProducts();
        return optionalProducts.orElse(new ArrayList<Product>());
    }

    @Override
    public boolean existsById(long id) {
        return productRepository.existsById(id);
    }

    @Override
    public Page<Product> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        return productRepository.findAllActiveProducts(pageable);
    }

    @Override
    public long findOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }

    @Override
    public long findTotalNotDeletedProducts() {
        return productRepository.findTotalNotDeletedProducts();
    }

    @Override
    public long findActiveProducts() {
        return productRepository.findActiveProducts();
    }

    @Override
    public long findInActiveProducts() {
        return productRepository.findInActiveProducts();
    }

    @Override
    public void update(Product product) {
        productRepository.save(product);
    }

    @Override
    public List<Object[]> getProductsStatsBetweenDates(Date fromDate, Date toDate) {
        return productRepository.getProductsStatsForConfirmedOrdersBetweenDates(fromDate,toDate);
    }

    @Override
    public List<Object[]> getProductStats() {
        return productRepository.getProductStatsForConfirmedOrders();
    }
}
