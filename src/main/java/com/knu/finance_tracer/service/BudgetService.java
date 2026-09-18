package com.knu.finance_tracer.service;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.repository.BudgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Бюджет не знайдено"));
    }

    public List<Budget> getBudgetsByUserId(Long userId) {
        return budgetRepository.findAllByUserId(userId);
    }

    @Transactional
    public Budget updateBudget(Long id, Budget budgetDetails) {
        Budget budget = getBudgetById(id);
        budget.setLimitAmount(budgetDetails.getLimitAmount());
        budget.setMonth(budgetDetails.getMonth());
        budget.setCategory(budgetDetails.getCategory());
        return budgetRepository.save(budget);
    }

    @Transactional
    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
}
