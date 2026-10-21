package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(ControllerTestConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private CategoryService categoryService;
    @MockitoBean private SecurityUtil securityUtil;
    @MockitoBean private CacheManager cacheManager;

    private User regularUser;
    private User adminUser;
    private Category category;

    @BeforeEach
    void setUp() {
        regularUser = new User(); regularUser.setId(1L); regularUser.setRole(Role.ROLE_USER);
        adminUser = new User(); adminUser.setId(2L); adminUser.setRole(Role.ROLE_ADMIN);

        category = new Category();
        category.setId(1L);
        category.setUser(regularUser);
        category.setName("Food");
        category.setIsExpense(false);
    }

    @Test
    @WithMockUser
    void listCategories_asUser() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(categoryService.getCategories(eq(1L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(category)));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/list"));
    }

    @Test
    @WithMockUser
    void listCategories_asAdmin() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(adminUser);
        when(categoryService.getAllCategoriesForAdmin(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(category)));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/list"));
    }

    @Test
    @WithMockUser
    void showCreateForm_asAdmin() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(adminUser);

        mockMvc.perform(get("/categories/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/create"));
    }

    @Test
    @WithMockUser
    void showCreateForm_asUser_shouldRedirect() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);

        mockMvc.perform(get("/categories/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));
    }

    @Test
    @WithMockUser
    void createCategory_asAdmin() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(adminUser);

        mockMvc.perform(post("/categories").with(csrf()).flashAttr("category", category))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));
    }

    @Test
    @WithMockUser
    void createCategory_asUser_shouldRedirect() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);

        mockMvc.perform(post("/categories").with(csrf()).flashAttr("category", category))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));
    }

    @Test
    @WithMockUser
    void showEditForm_owner() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/categories/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/edit"));
    }

    @Test
    @WithMockUser
    void showEditForm_forbidden() throws Exception {
        User another = new User(); another.setId(99L); another.setRole(Role.ROLE_USER);
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(another);
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/categories/edit/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateCategory() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(post("/categories/update/1").with(csrf()).flashAttr("category", category))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));
    }

    @Test
    @WithMockUser
    void deleteCategory() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(post("/categories/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));
    }
}
