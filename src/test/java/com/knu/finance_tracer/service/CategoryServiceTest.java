package com.knu.finance_tracer.service;

import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.repository.CategoryRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_Success() {
        Category category = new Category();
        when(categoryRepository.save(any())).thenReturn(category);
        assertNotNull(categoryService.createCategory(new Category()));
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        assertNotNull(categoryService.getCategoryById(1L));
    }

    @Test
    void getCategoryById_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void getCategoriesByUserId_Success() {
        when(categoryRepository.findAllByUserIdAndIsDeletedFalse(1L)).thenReturn(List.of(new Category()));
        assertEquals(1, categoryService.getCategoriesByUserId(1L).size());
    }

    @Test
    void getCategories_WithPageable_Success() {
        Page<Category> page = new PageImpl<>(List.of(new Category()));
        when(categoryRepository.findAllByUserIdAndIsDeletedFalse(eq(1L), any(Pageable.class))).thenReturn(page);

        assertEquals(1, categoryService.getCategories(1L, PageRequest.of(0, 5)).getContent().size());
    }

    @Test
    void updateCategory_Success() {
        Category existing = new Category();
        Category details = new Category();
        details.setName("Їжа");
        details.setIsExpense(true);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any())).thenReturn(existing);

        categoryService.updateCategory(1L, details);

        assertEquals("Їжа", existing.getName());
        assertTrue(existing.getIsExpense());
    }

    @Test
    void deleteCategory_Success() {
        Category category = new Category();
        category.setIsDeleted(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        assertTrue(category.getIsDeleted());
        verify(categoryRepository).save(category);
    }
}
