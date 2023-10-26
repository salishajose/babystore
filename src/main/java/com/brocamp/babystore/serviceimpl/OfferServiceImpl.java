package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.dto.OfferDTO;
import com.brocamp.babystore.model.Category;
import com.brocamp.babystore.model.Offer;
import com.brocamp.babystore.model.Product;
import com.brocamp.babystore.repository.OfferRepository;
import com.brocamp.babystore.repository.ProductRepository;
import com.brocamp.babystore.service.CategoryService;
import com.brocamp.babystore.service.OfferService;
import com.brocamp.babystore.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class OfferServiceImpl implements OfferService {
    private OfferRepository offerRepository;
    private ProductService productService;
    private CategoryService categoryService;
    private ProductRepository productRepository;
    @Override
    public List<Offer> findAllOffers() {
        Optional<List<Offer>> optionalOfferList = offerRepository.findByDeletedFalse();
        return optionalOfferList.orElseThrow(()-> new RuntimeException("Couldn't fetch data"));
    }

    @Override
    public Offer SaveOffer(OfferDTO offerDTO) {
        Offer offer = new Offer();
        offer.setName(offerDTO.getName());
        offer.setDescription(offerDTO.getDescription());
        offer.setOffPercentage(offerDTO.getOffPercentage());
        offer.setOfferType(offerDTO.getOfferType());
        offer.setEnabled(true);
        if(offerDTO.getOfferType().equals("Product")){
            Product product=productService.findById(offerDTO.getOfferProductId());
            offer.setOfferProductId(offerDTO.getOfferProductId());
            Double oldDiscount= (Double)product.getCostPrice() * ((double)offerDTO.getOffPercentage()/100.0);
            String formattedDiscount = String.format("%.2f",oldDiscount);
            Double discount= Double.parseDouble(formattedDiscount);
            String formattedSalePrice = String.format("%.2f", product.getCostPrice() - discount);
            Double salePrice= Double.parseDouble(formattedSalePrice);
            product.setSalePrice(salePrice);
            offer.setApplicableForProductName(product.getName());
            productRepository.save(product);
        }else{
            long applicable_id=offerDTO.getOfferCategoryId();
            Category category= categoryService.findById(applicable_id);
            offer.setOfferCategoryId(offerDTO.getOfferCategoryId());
            offer.setApplicableForCategoryName(category.getName());
            List<Product> productList = productService.findAllByCategoryId(category.getId());
            for(Product product : productList){
                Double oldDiscount= (double)product.getCostPrice() * ((double)offerDTO.getOffPercentage()/100.0);
                String formattedDiscount = String.format("%.2f",oldDiscount);
                Double discount= Double.parseDouble(formattedDiscount);
                String formattedSalePrice = String.format("%.2f", product.getCostPrice() - discount);
                Double salePrice= Double.parseDouble(formattedSalePrice);
                product.setSalePrice(salePrice);
                productRepository.save(product);
            }

        }

        offerRepository.save(offer);


        return offer;
    }

    @Override
    public Offer findById(long id) {
        return offerRepository.findById(id).orElseThrow(()->new RuntimeException("No offer found with id : "+id));
    }

    @Override
    public Offer update(Offer offerDto) {
        long id=offerDto.getId();
        Offer offer=offerRepository.findById(id).orElseThrow(()->new RuntimeException("couldn't find offer with id : "+id));

        offer.setName(offerDto.getName());
        offer.setDescription(offerDto.getDescription());
        offer.setOffPercentage(offerDto.getOffPercentage());
        offer.setOfferType(offerDto.getOfferType());
        if(offerDto.getOfferType().equals("Product")){
            if(offer.getOfferProductId() != null) {
                if (offerDto.getOfferProductId() != offer.getOfferProductId()) {
                    updateProductPrice(offer.getOfferProductId());
                }
            }else{
                updateCategoryPrice(offer.getOfferCategoryId());
                offer.setOfferCategoryId(null);
            }
            Product product=productService.findById(offerDto.getOfferProductId());
            offer.setOfferProductId(offerDto.getOfferProductId());
            Double oldDiscount= (Double)product.getCostPrice() * ((double)offerDto.getOffPercentage()/100.0);
            String formattedDiscount = String.format("%.2f",oldDiscount);
            Double discount= Double.parseDouble(formattedDiscount);
            String formattedSalePrice = String.format("%.2f", product.getCostPrice() - discount);
            Double salePrice= Double.parseDouble(formattedSalePrice);
            product.setSalePrice(salePrice);
            offer.setApplicableForProductName(product.getName());
            productRepository.save(product);
        }else{
            if(offer.getOfferCategoryId() != null) {
                if (offerDto.getOfferCategoryId() != offer.getOfferCategoryId()) {
                    updateCategoryPrice(offer.getOfferCategoryId());
                }
            }else{
                updateProductPrice(offer.getOfferProductId());
                offer.setOfferProductId(null);
            }
            long applicable_id=offerDto.getOfferCategoryId();
            Category category= categoryService.findById(applicable_id);
            offer.setOfferCategoryId(offerDto.getOfferCategoryId());
            offer.setApplicableForCategoryName(category.getName());
            List<Product> productList = productService.findAllByCategoryId(category.getId());
            for(Product product : productList){
                Double oldDiscount= (Double)product.getCostPrice() * ((double)offerDto.getOffPercentage()/100.0);
                String formattedDiscount = String.format("%.2f",oldDiscount);
                Double discount= Double.parseDouble(formattedDiscount);
                String formattedSalePrice = String.format("%.2f", product.getCostPrice() - discount);
                Double salePrice= Double.parseDouble(formattedSalePrice);
                product.setSalePrice(salePrice);
                productRepository.save(product);
            }


        }

        offerRepository.save(offer);


        return offer;
    }

    @Override
    public void disable(long id) {
        Offer offer=offerRepository.findById(id).orElseThrow();
        offer.setEnabled(false);
        if (offer.getOfferType().equals("Product")){
            Product product=productService.findById(offer.getOfferProductId());
            product.setSalePrice(product.getCostPrice());
            productRepository.save(product);
        }else{
            long applicable_id=offer.getOfferCategoryId();
            Category category= categoryService.findById(applicable_id);
            List<Product> productList = productService.findAllByCategoryId(category.getId());
            for(Product product : productList){
                product.setSalePrice(product.getCostPrice());
                productRepository.save(product);
            }

        }

    }

    @Override
    public void enable(long id) {
        Offer offer=offerRepository.findById(id).orElseThrow();
        offer.setEnabled(true);
        if (offer.getOfferType().equals("Product")){
            Product product=productService.findById(offer.getOfferProductId());
            Double oldDiscount= (Double)product.getCostPrice() * ((double)offer.getOffPercentage()/100.0);
            String formattedDiscount = String.format("%.2f",oldDiscount);
            Double discount= Double.parseDouble(formattedDiscount);
            String formattedSalePrice = String.format("%.2f", product.getCostPrice() - discount);
            Double salePrice= Double.parseDouble(formattedSalePrice);
            product.setSalePrice(salePrice);
            productRepository.save(product);
        }else{
            long applicable_id=offer.getOfferCategoryId();
            Category category= categoryService.findById(applicable_id);
            List<Product> productList = productService.findAllByCategoryId(category.getId());
            for(Product product : productList){
                Double oldDiscount= (Double)product.getCostPrice() * ((double)offer.getOffPercentage()/100.0);
                String formattedDiscount = String.format("%.2f",oldDiscount);
                Double discount= Double.parseDouble(formattedDiscount);
                String formattedSalePrice = String.format("%.2f", product.getCostPrice() - discount);
                Double salePrice= Double.parseDouble(formattedSalePrice);
                product.setSalePrice(salePrice);
                productRepository.save(product);
            }

        }

    }

    @Override
    public void deleteOffer(long id) {
        Offer offer=offerRepository.findById(id).orElseThrow();

        if(offer.getOfferType().equals("Product")){
            Product product=productService.findById(offer.getOfferProductId());
            if(product!=null) {
                product.setSalePrice(product.getCostPrice());
                productRepository.save(product);
            }
        }else{
            long applicable_id=offer.getOfferCategoryId();
            Category category= categoryService.findById(applicable_id);
            List<Product> productList = productService.findAllByCategoryId(category.getId());
            for(Product product : productList){
                if(product !=null) {
                    product.setSalePrice(product.getCostPrice());
                    productRepository.save(product);
                }
            }
        }
        offer.setDeleted(true);
        offerRepository.save(offer);

    }

    public void updateProductPrice(long id){
        Product product=productService.findById(id);
        product.setSalePrice(product.getCostPrice());
        productRepository.save(product);

    }
    public void updateCategoryPrice(long id){

        Category category= categoryService.findById(id);
        List<Product> productList = productService.findAllByCategoryId(category.getId());
        for(Product product : productList){
            product.setSalePrice(product.getCostPrice());
            productRepository.save(product);
        }

    }
}
