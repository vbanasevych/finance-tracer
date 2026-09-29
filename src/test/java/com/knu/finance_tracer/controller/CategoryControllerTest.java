package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock private CategoryService categoryService;
    @Mock private UserService userService;
    @Mock private Model model;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void listCategories_ShouldReturnView() {
        Page<Category> page = new PageImpl<>(List.of(new Category(), new Category()));
        when(categoryService.getCategories(eq(1L), any(Pageable.class))).thenReturn(page);
        String view = categoryController.listCategories(0, 5, model);
        assertEquals("categories/list", view);
        verify(model).addAttribute(eq("page"), any());
    }

    @Test
    void showCreateForm_ShouldReturnView() {
        String view = categoryController.showCreateForm(model);
        assertEquals("categories/create", view);
        verify(model).addAttribute(eq("category"), any(Category.class));
    }

    @Test
    void createCategory_ShouldSaveAndRedirect() {
        Category category = new Category();
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);

        String view = categoryController.createCategory(category);
        assertEquals("redirect:/categories", view);
        verify(categoryService).createCategory(category);
    }

    @Test
    void showEditForm_ShouldReturnView() {
        when(categoryService.getCategoryById(1L)).thenReturn(new Category());
        String view = categoryController.showEditForm(1L, model);
        assertEquals("categories/edit", view);
        verify(model).addAttribute(eq("category"), any(Category.class));
    }

    @Test
    void updateCategory_ShouldUpdateAndRedirect() {
        String view = categoryController.updateCategory(1L, new Category());
        assertEquals("redirect:/categories", view);
        verify(categoryService).updateCategory(eq(1L), any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteAndRedirect() {
        String view = categoryController.deleteCategory(1L);
        assertEquals("redirect:/categories", view);
        verify(categoryService).deleteCategory(1L);
    }
}
