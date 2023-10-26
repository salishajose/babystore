package com.brocamp.babystore.repository;

import com.brocamp.babystore.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findAllByProductDeletedFalse();

    List<Product> findAllByProductActivatedTrue();

    List<Product> findAllByCategoryId(Long id);

    List<Product> findAllByNameContainingIgnoreCase(String name);

    List<Product> findAllByProductActivated(boolean b);

    @Query("select p from Product p where p.productDeleted=false and p.productActivated=true and p.category.isDelete=false")
    Optional<List<Product>> findCurrentProducts();

    @Query("select p from Product p where p.productDeleted=false and p.productActivated=true and p.category.isDelete=false")
    Page<Product> findAllActiveProducts(Pageable pageable);

    @Query("select COUNT(p) from Product p where p.productDeleted=false and p.productActivated=true and p.currentQuantity=0")
    long findOutOfStockProducts();

    @Query("select COUNT(p) from Product p where p.productDeleted=false")
    long findTotalNotDeletedProducts();

    @Query("select COUNT(p) from Product p where p.productDeleted=false and p.productActivated=true")
    long findActiveProducts();

    @Query("select COUNT(p) from Product p where p.productDeleted=false and p.productActivated=false")
    long findInActiveProducts();

    @Query("select p.id,p.name,c.name,sum(op.quantity) as total_quantity_ordered,sum(op.totalRate) AS total_revenue " +
            " from Product p " +
            "join OrderProducts op on p.id=op.product.id " +
            "join OrderDetails o on op.orderDetails.id = o.id " +
            "join Category c on p.category.id = c.id group by p.id,p.name,c.name order by total_revenue")
    List<Object[]> getProductsStatsForConfirmedOrdersBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("select p.id,p.name,c.name,sum(op.quantity) as total_quantity_ordered,sum(o.orderAmount) AS total_revenue" +
            " from Product p join OrderProducts op on p.id=op.product.id " +
            "join OrderDetails o on op.orderDetails.id=o.id join Category c on p.category.id = c.id " +
            "where o.orderStatus='DELIVERED' " +
            " group by p.id,p.name,c.name ORDER BY total_revenue DESC")
    List<Object[]> getProductStatsForConfirmedOrders();
}
