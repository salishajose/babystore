package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.model.Category;
import com.brocamp.babystore.repository.CategoryRepository;
import com.brocamp.babystore.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean existByName(String name) throws Exception {
        try{
            return categoryRepository.existsByNameIgnoreCase(name);
        }catch(Exception e){
            log.info("Couldn't check category name already exists");
            throw new Exception("Couldn't check category name already exists");
        }
    }

    @Override
    public void saveOrUpdate(Category category) throws Exception {
        try{
            categoryRepository.save(category);
        }catch (Exception e){
            log.info("Internal server error: couldn't save category");
            throw new Exception("Couldn't save category");
            //throw new
        }
    }

    @Override
    public List<Category> findAllCategories() throws Exception {
        try{
            Optional<List<Category>> optionalCategoryList = categoryRepository.findByIsDelete(false);
            //Optional<List<Category>> optionalCategoryList = Optional.ofNullable(categoryRepository.findAll());
            return optionalCategoryList.orElse(new ArrayList<Category>());
        }catch(Exception e){
            log.info("Couldn't fetch non deleted categories");
            throw new Exception("Couldn't fetch non deleted categories");
        }
    }

    @Override
    public boolean existsById(long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public Category findById(long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        return optionalCategory.orElse(null);
    }

    @Override
    public Page<Category> findAllCategoriesPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        return categoryRepository.findAllCategoriesPaginated(pageable);
    }

    @Override
    public long findTotalCategoryCount() {
        return categoryRepository.findTotalCategoryCount();
    }

    @Override
    public long findBlockedCategoryCount() {
        return categoryRepository.findBlockedCategoryCount();
    }

    @Override
    public long findUnblockedCategoryCount() {
        return categoryRepository.findUnblockedCategoryCount();
    }

    @Override
    public List<Category> findAllCategoriesByName(String name) {
        return categoryRepository.findAllCategoriesByName(name);
    }

    @Override
    public List<Category> findAllCurrentCategories() {
        Optional<List<Category>> optionalCategoryList = categoryRepository.findAllCurrentCategories();
        return optionalCategoryList.orElseThrow(()->new RuntimeException("COuldn't fet category details"));
    }
}
