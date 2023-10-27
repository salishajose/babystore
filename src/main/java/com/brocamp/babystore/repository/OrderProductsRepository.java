package com.brocamp.babystore.repository;

import com.brocamp.babystore.model.OrderProducts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderProductsRepository extends JpaRepository<OrderProducts,Long> {
    @Query("from OrderProducts op where op.orderDetails.users.id=?1")
    Optional<List<OrderProducts>> findAllByUsersId(long usersId);

    @Query("from OrderProducts op where op.orderDetails.id=?1")
    List<OrderProducts> findAllByOrderDetailsId(long id);
    @Query("from OrderProducts op where op.orderDetails.id=?1")
    Optional<List<OrderProducts>> findByOrderDetailsId(long id);

    @Query("from OrderProducts op where op.orderDetails.users.id=?1")
    Page<OrderProducts> findAllByUsersIdPaginated(long userId,Pageable pageable);

    @Query("SELECT op.product.id, SUM(op.quantity) AS qty, SUM(op.totalRate) " +
            "FROM OrderProducts op " +
            "WHERE op.orderDetails.orderStatus = ?1 AND EXTRACT(day FROM op.orderDetails.deliveryDate) = EXTRACT(day FROM CAST(?2 AS DATE))" +
            " and EXTRACT(month FROM op.orderDetails.deliveryDate) = EXTRACT(month FROM CAST(?2 AS DATE)) and " +
            " EXTRACT(year FROM op.orderDetails.deliveryDate) = EXTRACT(year FROM CAST(?2 AS DATE)) " +
            "GROUP BY op.product.id " +
            "ORDER BY SUM(op.quantity) DESC")
    List<Object[]> getAllProductSalesByDate(String status,Date date);

    @Query("SELECT op.product.id, coalesce(SUM(op.quantity),0) AS qty, SUM(op.totalRate) " +
            "FROM OrderProducts op " +
            "WHERE op.orderDetails.orderStatus = ?1 " +
            " and EXTRACT(month FROM op.orderDetails.deliveryDate) =?2 and " +
            " EXTRACT(year FROM op.orderDetails.deliveryDate) = ?3 " +
            "GROUP BY op.product.id " +
            "ORDER BY SUM(op.quantity) DESC")
    List<Object[]> getAllProductSalesByMonthwise(String status, int month, int year);

    @Query("SELECT op.product.id, coalesce(SUM(op.quantity),0)  AS qty, SUM(op.totalRate) " +
            "FROM OrderProducts op " +
            "WHERE op.orderDetails.orderStatus = ?1  and " +
            " EXTRACT(year FROM op.orderDetails.deliveryDate) = ?2 " +
            "GROUP BY op.product.id " +
            "ORDER BY SUM(op.quantity) DESC")
    List<Object[]> getAllProductSalesByYearwise(String status, int selectedYear);
}
