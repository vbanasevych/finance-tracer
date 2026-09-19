package com.knu.finance_tracer.service;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.repository.BudgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void createBudget_Success() {
        Budget budget = new Budget();
        when(budgetRepository.save(any())).thenReturn(budget);
        assertNotNull(budgetService.createBudget(new Budget()));
    }

    @Test
    void getBudgetById_Success() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(new Budget()));
        assertNotNull(budgetService.getBudgetById(1L));
    }

    @Test
    void getBudgetById_NotFound() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> budgetService.getBudgetById(1L));
    }

    @Test
    void getBudgetsByUserId_Success() {
        when(budgetRepository.findAllByUserId(1L)).thenReturn(List.of(new Budget()));
        assertEquals(1, budgetService.getBudgetsByUserId(1L).size());
    }

    @Test
    void updateBudget_Success() {
        Budget existing = new Budget();
        Budget details = new Budget();
        details.setLimitAmount(BigDecimal.valueOf(5000));
        details.setMonth(LocalDate.now());

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(budgetRepository.save(any())).thenReturn(existing);

        budgetService.updateBudget(1L, details);

        assertEquals(BigDecimal.valueOf(5000), existing.getLimitAmount());
        assertEquals(details.getMonth(), existing.getMonth());
    }

    @Test
    void deleteBudget_Success() {
        budgetService.deleteBudget(1L);
        verify(budgetRepository).deleteById(1L);
    }
}
