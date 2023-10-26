package com.brocamp.babystore.repository;

import com.brocamp.babystore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Long> {

    @Query("SELECT sc from ShoppingCart sc where sc.users.id=?1 and sc.product.id=?2 and sc.deleted=false")
    ShoppingCart findByUsersProduct(long userId, long productId);

//    @Query("SELECT SC FROM ShoppingCart sc where sc.users.id=?1 and sc.deleted=false")
//    List<ShoppingCart> getSHoppingCartProductsByUsersId(long usersId);

    @Query("FROM ShoppingCart sc where sc.users.id=?1 and sc.deleted=false")
    List<ShoppingCart> findAllByUsersId(long usersId);

    @Query("SELECT SUM(sc.quantity),SUM(sc.totalRate) FROM ShoppingCart sc where sc.users.id=?1 and sc.deleted=false")
    List<Object[]> getQuantitySumAndTotalRateSum(long usersId);
    @Query("SELECT COALESCE(SUM(sc.quantity), 0) FROM ShoppingCart sc where sc.users.id=?1 and sc.deleted=false")
    long getTotalItemsInCartByUsersId(long id);
}
