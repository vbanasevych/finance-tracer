package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.service.BudgetService;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock private BudgetService budgetService;
    @Mock private CategoryService categoryService;
    @Mock private UserService userService;
    @Mock private Model model;

    @InjectMocks
    private BudgetController budgetController;

    @Test
    void listBudgets_ShouldReturnView() {
        when(budgetService.getBudgetsByUserId(1L)).thenReturn(List.of(new Budget()));
        String view = budgetController.listBudgets(model);
        assertEquals("budgets/list", view);
        verify(model).addAttribute(eq("budgets"), anyList());
    }

    @Test
    void showCreateForm_ShouldReturnView() {
        when(categoryService.getCategoriesByUserId(1L)).thenReturn(List.of());
        String view = budgetController.showCreateForm(model);
        assertEquals("budgets/create", view);
        verify(model).addAttribute(eq("budget"), any(Budget.class));
        verify(model).addAttribute(eq("categories"), anyList());
    }

    @Test
    void createBudget_ShouldSaveAndRedirect() {
        Budget budget = new Budget();
        when(userService.getUserById(1L)).thenReturn(new User());

        String view = budgetController.createBudget(budget);
        assertEquals("redirect:/budgets", view);
        verify(budgetService).createBudget(budget);
    }

    @Test
    void showEditForm_ShouldReturnView() {
        when(budgetService.getBudgetById(1L)).thenReturn(new Budget());
        when(categoryService.getCategoriesByUserId(1L)).thenReturn(List.of());

        String view = budgetController.showEditForm(1L, model);
        assertEquals("budgets/edit", view);
        verify(model).addAttribute(eq("budget"), any(Budget.class));
        verify(model).addAttribute(eq("categories"), anyList());
    }

    @Test
    void updateBudget_ShouldUpdateAndRedirect() {
        String view = budgetController.updateBudget(1L, new Budget());
        assertEquals("redirect:/budgets", view);
        verify(budgetService).updateBudget(eq(1L), any(Budget.class));
    }

    @Test
    void deleteBudget_ShouldDeleteAndRedirect() {
        String view = budgetController.deleteBudget(1L);
        assertEquals("redirect:/budgets", view);
        verify(budgetService).deleteBudget(1L);
    }
}
