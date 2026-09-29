package com.knu.finance_tracer.api;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.service.BudgetService;
import com.knu.finance_tracer.service.UserService;
import org.junit.jupiter.api.Test;
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
class BudgetApiControllerTest {

    @Mock private BudgetService budgetService;
    @Mock private UserService userService;

    @InjectMocks
    private BudgetApiController controller;

    @Test
    void getBudgets_ShouldReturnPageAnd200() {
        Page<Budget> page = new PageImpl<>(List.of(new Budget()));
        when(budgetService.getBudgets(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<Budget>> response = controller.getBudgets(0, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void getBudgetById_ShouldReturnBudgetAnd200() {
        Budget budget = new Budget();
        when(budgetService.getBudgetById(1L)).thenReturn(budget);

        ResponseEntity<Budget> response = controller.getBudgetById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budget, response.getBody());
    }

    @Test
    void createBudget_ShouldReturnCreatedBudgetAnd201() {
        Budget budget = new Budget();
        when(userService.getUserById(1L)).thenReturn(new User());
        when(budgetService.createBudget(any())).thenReturn(budget);

        ResponseEntity<Budget> response = controller.createBudget(new Budget());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(budget, response.getBody());
    }

    @Test
    void updateBudget_ShouldReturnUpdatedBudgetAnd200() {
        Budget budget = new Budget();
        when(budgetService.updateBudget(eq(1L), any())).thenReturn(budget);

        ResponseEntity<Budget> response = controller.updateBudget(1L, new Budget());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budget, response.getBody());
    }

    @Test
    void deleteBudget_ShouldReturn204() {
        ResponseEntity<Void> response = controller.deleteBudget(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(budgetService).deleteBudget(1L);
    }
}
