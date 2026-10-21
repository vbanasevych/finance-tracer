package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.BudgetService;
import com.knu.finance_tracer.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
@Import(ControllerTestConfig.class)
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BudgetService budgetService;
    @MockitoBean private CategoryService categoryService;
    @MockitoBean private SecurityUtil securityUtil;
    @MockitoBean private CacheManager cacheManager;

    private User regularUser;
    private User adminUser;
    private Budget budget;

    @BeforeEach
    void setUp() {
        regularUser = new User(); regularUser.setId(1L); regularUser.setRole(Role.ROLE_USER);
        adminUser = new User(); adminUser.setId(2L); adminUser.setRole(Role.ROLE_ADMIN);

        budget = new Budget();
        budget.setId(1L);
        budget.setUser(regularUser);
        Category budgetCategory = new Category();
        budgetCategory.setId(1L);
        budgetCategory.setName("Food");
        budget.setCategory(budgetCategory);
    }

    @Test
    @WithMockUser
    void listBudgets_asUser() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(budgetService.getBudgets(eq(1L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(budget)));

        mockMvc.perform(get("/budgets"))
                .andExpect(status().isOk())
                .andExpect(view().name("budgets/list"));
    }

    @Test
    @WithMockUser
    void listBudgets_asAdmin() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(adminUser);
        when(budgetService.getAllBudgetsForAdmin(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(budget)));

        mockMvc.perform(get("/budgets"))
                .andExpect(status().isOk())
                .andExpect(view().name("budgets/list"));
    }

    @Test
    @WithMockUser
    void showCreateForm() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(categoryService.getCategoriesByUserId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/budgets/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("budgets/create"));
    }

    @Test
    @WithMockUser
    void createBudget() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);

        mockMvc.perform(post("/budgets").with(csrf()).flashAttr("budget", budget))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/budgets"));
    }

    @Test
    @WithMockUser
    void showEditForm_owner() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(budgetService.getBudgetById(1L)).thenReturn(budget);
        when(categoryService.getCategoriesByUserId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/budgets/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("budgets/edit"));
    }

    @Test
    @WithMockUser
    void showEditForm_forbidden() throws Exception {
        User another = new User(); another.setId(99L); another.setRole(Role.ROLE_USER);
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(another);
        when(budgetService.getBudgetById(1L)).thenReturn(budget);

        mockMvc.perform(get("/budgets/edit/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateBudget() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(budgetService.getBudgetById(1L)).thenReturn(budget);

        mockMvc.perform(post("/budgets/update/1").with(csrf()).flashAttr("budget", budget))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/budgets"));
    }

    @Test
    @WithMockUser
    void deleteBudget() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(budgetService.getBudgetById(1L)).thenReturn(budget);

        mockMvc.perform(post("/budgets/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/budgets"));
    }
}
