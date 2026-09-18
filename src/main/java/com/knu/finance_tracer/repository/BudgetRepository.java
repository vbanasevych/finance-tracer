package com.knu.finance_tracer.repository;


import com.knu.finance_tracer.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findAllByUserId(Long userId);
}
