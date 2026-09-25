package com.knu.finance_tracer.service;

import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.repository.CategoryRepository;
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

    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Категорію не знайдено"));
    }

    public List<Category> getCategoriesByUserId(Long userId) {
        return categoryRepository.findAllByUserIdAndIsDeletedFalse(userId);
    }

    public Page<Category> getCategories(Long userId, Pageable pageable) {
        return categoryRepository.findAllByUserIdAndIsDeletedFalse(userId, pageable);
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);
        category.setName(categoryDetails.getName());
        category.setIsExpense(categoryDetails.getIsExpense());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        category.setIsDeleted(true);
        categoryRepository.save(category);
    }
}
