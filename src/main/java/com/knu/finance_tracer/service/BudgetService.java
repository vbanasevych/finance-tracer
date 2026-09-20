package com.knu.finance_tracer.service;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.entity.Transaction;
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

    private void validateDates(java.time.LocalDate start, java.time.LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("Дата початку не може бути пізнішою за дату завершення");
        }
    }

    @Transactional
    public Budget createBudget(Budget budget) {
        validateDates(budget.getStartDate(), budget.getEndDate());
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
        validateDates(budgetDetails.getStartDate(), budgetDetails.getEndDate());

        Budget budget = getBudgetById(id);
        budget.setLimitAmount(budgetDetails.getLimitAmount());
        budget.setStartDate(budgetDetails.getStartDate());
        budget.setEndDate(budgetDetails.getEndDate());
        budget.setCategory(budgetDetails.getCategory());
        return budgetRepository.save(budget);
    }

    @Transactional
    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    public List<String> getBudgetWarnings(Long userId, List<Transaction> transactions) {
        List<Budget> budgets = getBudgetsByUserId(userId);
        java.util.List<String> warnings = new java.util.ArrayList<>();

        for (Budget budget : budgets) {
            java.math.BigDecimal totalSpent = transactions.stream()
                    .filter(t -> t.getCategory().getId().equals(budget.getCategory().getId()))
                    .filter(t -> !t.getDateTime().toLocalDate().isBefore(budget.getStartDate()) &&
                            !t.getDateTime().toLocalDate().isAfter(budget.getEndDate()))
                    .map(com.knu.finance_tracer.entity.Transaction::getAmount)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            if (totalSpent.compareTo(budget.getLimitAmount()) > 0) {
                warnings.add(String.format("Перевищено бюджет для '%s'! Ліміт: %s, Витрачено: %s",
                        budget.getCategory().getName(), budget.getLimitAmount(), totalSpent));
            }
        }
        return warnings;
    }
}
