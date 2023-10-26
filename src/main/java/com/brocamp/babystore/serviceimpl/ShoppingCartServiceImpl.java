package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.model.Product;
import com.brocamp.babystore.model.ShoppingCart;
import com.brocamp.babystore.repository.ShoppingCartRepository;
import com.brocamp.babystore.repository.UsersRepository;
import com.brocamp.babystore.service.ProductService;
import com.brocamp.babystore.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private ProductService productService;
    private ShoppingCartRepository shoppingCartRepository;
    private UsersRepository usersRepository;

    public ShoppingCartServiceImpl(ProductService productService,
                                   ShoppingCartRepository shoppingCartRepository,
                                   UsersRepository usersRepository) {
        this.productService = productService;
        this.shoppingCartRepository = shoppingCartRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public void addtoCart(long userId, long productId, long quantity) {

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsersProduct(userId,productId);
        Product product = productService.findById(productId);
        if(shoppingCart!=null){
            long oldQuantity = shoppingCart.getQuantity();
            shoppingCart.setQuantity(oldQuantity+quantity);
            shoppingCart.setIndividualRate(product.getSalePrice());
            double totalRate = product.getSalePrice() * (oldQuantity+quantity);
            shoppingCart.setTotalRate(totalRate);
        }else {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUsers(usersRepository.findById(userId));
            shoppingCart.setProduct(product);
            shoppingCart.setQuantity(quantity);
            shoppingCart.setIndividualRate(product.getSalePrice());
            double totalRate = product.getSalePrice() * quantity;
            shoppingCart.setTotalRate(totalRate);
        }
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public List<ShoppingCart> getSHoppingCartProductsByUsersId(long usersId) {
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAllByUsersId(usersId);
        //Optional<> optionalShoppingCartList = Optional.ofNullable();
        return shoppingCartList;
    }

    @Override
    public void removeProductFromCart(long usersId,long productId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsersProduct(usersId,productId);
        shoppingCart.setDeleted(true);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public List<Object[]> getQuantitySumAndTotalRateSum(long usersId){
        return shoppingCartRepository.getQuantitySumAndTotalRateSum(usersId);
    }

    @Override
    public void updateCartQuantity(long userId, long productId, long quantity) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsersProduct(userId,productId);
        if(shoppingCart!=null){
            if(shoppingCart.getQuantity()!=quantity){
                Product product = productService.findById(productId);
                shoppingCart.setQuantity(quantity);
                shoppingCart.setIndividualRate(product.getSalePrice());
                double totalRate = product.getSalePrice() * quantity;
                shoppingCart.setTotalRate(totalRate);
                shoppingCartRepository.save(shoppingCart);
            }
        }
    }

    @Override
    public long getTotalItemsInCartByUsersId(long id) {
        return shoppingCartRepository.getTotalItemsInCartByUsersId(id);
    }
}
