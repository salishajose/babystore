package com.brocamp.babystore.service;

import com.brocamp.babystore.model.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addtoCart(long userId, long productId, long quantity);

    List<ShoppingCart> getSHoppingCartProductsByUsersId(long usersId);

    void removeProductFromCart(long usersId,long productId);

    List<Object[]> getQuantitySumAndTotalRateSum(long usersId);

    void updateCartQuantity(long userId, long productId, long quantity);

    long getTotalItemsInCartByUsersId(long id);
}
