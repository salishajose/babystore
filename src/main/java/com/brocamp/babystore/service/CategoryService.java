package com.brocamp.babystore.service;

import com.brocamp.babystore.model.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    boolean existByName(String name) throws Exception;

    void saveOrUpdate(Category category) throws Exception;

    List<Category> findAllCategories() throws Exception;

    boolean existsById(long id);

    Category findById(long id);

    Page<Category> findAllCategoriesPaginated(int pageNo, int pageSize);

    long findTotalCategoryCount();

    long findBlockedCategoryCount();

    long findUnblockedCategoryCount();

    List<Category> findAllCategoriesByName(String name);

    List<Category> findAllCurrentCategories();
}
