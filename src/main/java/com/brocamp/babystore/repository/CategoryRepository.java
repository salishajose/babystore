package com.brocamp.babystore.repository;

import com.brocamp.babystore.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Category c WHERE LOWER(c.name) = LOWER(?1)")
    Boolean existsByNameIgnoreCase(String name);

    Optional<List<Category>> findByIsDelete(boolean b);

    @Query("select c from Category c where c.isDelete=false and c.isBlocked=false")
    Page<Category> findAllCategoriesPaginated(Pageable pageable);

    @Query("select COUNT(c) from Category c where c.isDelete=false ")
    long findTotalCategoryCount();

    @Query("select COUNT(c) from Category c where c.isDelete=false and c.isBlocked=true")
    long findBlockedCategoryCount();

    @Query("select COUNT(c) from Category c where c.isDelete=false and c.isBlocked=false")
    long findUnblockedCategoryCount();
    @Query("select c from Category c where c.isDelete=false and c.isBlocked=false and lower(c.name) like LOWER(concat('%', ?1, '%'))")
    List<Category> findAllCategoriesByName(String name);

    @Query("select c from Category c where c.isDelete=false and c.isBlocked=false")
    Optional<List<Category>> findAllCurrentCategories();
}
