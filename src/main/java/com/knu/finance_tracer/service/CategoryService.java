package com.knu.finance_tracer.service;

import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.repository.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @CacheEvict(value = {"categoriesPage", "categoriesList"}, allEntries = true)
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Cacheable(value = "category", key = "#id")
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Категорію не знайдено"));
    }

    @Cacheable(value = "categoriesList", key = "#userId")
    public List<Category> getCategoriesByUserId(Long userId) {
        return categoryRepository.findAllByUserIdAndIsDeletedFalse(userId);
    }

    @Cacheable(value = "categoriesPage", key = "#userId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Category> getCategories(Long userId, Pageable pageable) {
        return categoryRepository.findAllByUserIdAndIsDeletedFalse(userId, pageable);
    }

    @CacheEvict(value = {"categoriesPage", "categoriesList", "category"}, allEntries = true)
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);
        category.setName(categoryDetails.getName());
        category.setIsExpense(categoryDetails.getIsExpense());
        return categoryRepository.save(category);
    }

    @CacheEvict(value = {"categoriesPage", "categoriesList", "category"}, allEntries = true)
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        category.setIsDeleted(true);
        categoryRepository.save(category);
    }
}
