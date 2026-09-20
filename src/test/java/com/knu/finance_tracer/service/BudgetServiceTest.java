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
    void createBudget_InvalidDates_ShouldThrowException() {
        Budget budget = new Budget();
        budget.setStartDate(LocalDate.now().plusDays(5));
        budget.setEndDate(LocalDate.now());

        assertThrows(IllegalArgumentException.class, () -> budgetService.createBudget(budget));
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
        details.setStartDate(LocalDate.now());
        details.setEndDate(LocalDate.now().plusMonths(1));

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(budgetRepository.save(any())).thenReturn(existing);

        budgetService.updateBudget(1L, details);

        assertEquals(BigDecimal.valueOf(5000), existing.getLimitAmount());
        assertEquals(details.getStartDate(), existing.getStartDate());
    }

    @Test
    void deleteBudget_Success() {
        budgetService.deleteBudget(1L);
        verify(budgetRepository).deleteById(1L);
    }

    @Test
    void getBudgetWarnings_ShouldReturnWarningsWhenExceeded() {
        Budget budget = new Budget();
        budget.setLimitAmount(java.math.BigDecimal.valueOf(1000));
        budget.setStartDate(java.time.LocalDate.now().minusDays(1));
        budget.setEndDate(java.time.LocalDate.now().plusDays(1));

        com.knu.finance_tracer.entity.Category cat = new com.knu.finance_tracer.entity.Category();
        cat.setId(1L);
        cat.setName("Кафе");
        budget.setCategory(cat);

        com.knu.finance_tracer.entity.Transaction t1 = new com.knu.finance_tracer.entity.Transaction();
        t1.setAmount(java.math.BigDecimal.valueOf(600));
        t1.setDateTime(java.time.LocalDateTime.now());
        t1.setCategory(cat);

        com.knu.finance_tracer.entity.Transaction t2 = new com.knu.finance_tracer.entity.Transaction();
        t2.setAmount(java.math.BigDecimal.valueOf(500));
        t2.setDateTime(java.time.LocalDateTime.now());
        t2.setCategory(cat);

        when(budgetRepository.findAllByUserId(1L)).thenReturn(List.of(budget));

        List<String> warnings = budgetService.getBudgetWarnings(1L, List.of(t1, t2));

        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("Перевищено бюджет"));
        assertTrue(warnings.get(0).contains("1100"));
    }
}
