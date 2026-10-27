package com.knu.finance_tracer.api;

import com.knu.finance_tracer.dto.CategoryExpenseDto;
import com.knu.finance_tracer.repository.TransactionRepository;
import com.knu.finance_tracer.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsApiController {

    private final TransactionRepository transactionRepository;

    public AnalyticsApiController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/expenses-by-category")
    public List<CategoryExpenseDto> getExpensesByCategory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return transactionRepository.getExpensesGroupedByCategory(userId);
    }
}
