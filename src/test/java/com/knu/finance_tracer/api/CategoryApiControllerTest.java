package com.knu.finance_tracer.api;

import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryApiControllerTest {

    @Mock private CategoryService categoryService;
    @Mock private UserService userService;
    @Mock private SecurityUtil securityUtil;

    @InjectMocks
    private CategoryApiController controller;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(Role.ROLE_USER);
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(currentUser);
    }

    @Test
    void getCategories_ShouldReturnPageAnd200() {
        Page<Category> page = new PageImpl<>(List.of(new Category()));
        when(categoryService.getCategories(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<Category>> response = controller.getCategories(0, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void getCategoryById_ShouldReturnCategoryAnd200() {
        Category cat = new Category();
        cat.setUser(currentUser);
        when(categoryService.getCategoryById(1L)).thenReturn(cat);

        ResponseEntity<Category> response = controller.getCategoryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cat, response.getBody());
    }

    @Test
    void createCategory_ShouldReturnCreatedCategoryAnd201() {
        Category cat = new Category();
        currentUser.setRole(Role.ROLE_ADMIN);
        when(categoryService.createCategory(any())).thenReturn(cat);

        ResponseEntity<Category> response = controller.createCategory(new Category());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cat, response.getBody());
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategoryAnd200() {
        Category cat = new Category();
        cat.setUser(currentUser);
        when(categoryService.getCategoryById(1L)).thenReturn(cat);
        when(categoryService.updateCategory(eq(1L), any())).thenReturn(cat);

        ResponseEntity<Category> response = controller.updateCategory(1L, new Category());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cat, response.getBody());
    }

    @Test
    void deleteCategory_ShouldReturn204() {
        Category cat = new Category();
        cat.setUser(currentUser);
        when(categoryService.getCategoryById(1L)).thenReturn(cat);
        ResponseEntity<Void> response = controller.deleteCategory(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService).deleteCategory(1L);
    }
}
