package com.knu.finance_tracer.service;

import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Продукти");
        category.setIsExpense(true);
        category.setIsDeleted(false);
    }

    @Test
    void createCategory_shouldReturnSavedCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category created = categoryService.createCategory(category);

        assertNotNull(created);
        assertEquals("Продукти", created.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void getCategoryById_shouldReturnCategory_whenExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category found = categoryService.getCategoryById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void getCategoryById_shouldThrowException_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.getCategoryById(99L);
        });

        assertEquals("Категорію не знайдено", exception.getMessage());
    }

    @Test
    void getCategoriesByUserId_shouldReturnList() {
        when(categoryRepository.findAllAvailableForUser(1L, Role.ROLE_ADMIN)).thenReturn(List.of(category));

        List<Category> list = categoryService.getCategoriesByUserId(1L);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void getCategories_pageable_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(List.of(category));
        when(categoryRepository.findAllAvailableForUser(1L, Role.ROLE_ADMIN, pageable)).thenReturn(page);

        Page<Category> result = categoryService.getCategories(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateCategory_shouldUpdateAndReturn() {
        Category details = new Category();
        details.setName("Оновлені продукти");
        details.setIsExpense(false);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category updated = categoryService.updateCategory(1L, details);

        assertNotNull(updated);
        assertEquals("Оновлені продукти", updated.getName());
        assertFalse(updated.getIsExpense());
    }

    @Test
    void deleteCategory_shouldMarkAsDeleted() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        categoryService.deleteCategory(1L);

        assertTrue(category.getIsDeleted());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void getAllCategoriesForAdmin_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(List.of(category));
        when(categoryRepository.findAll(pageable)).thenReturn(page);

        Page<Category> result = categoryService.getAllCategoriesForAdmin(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}
