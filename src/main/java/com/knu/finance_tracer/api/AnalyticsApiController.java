package com.knu.finance_tracer.api;

import com.knu.finance_tracer.dto.CategoryExpenseDto;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.repository.TransactionRepository;
import com.knu.finance_tracer.repository.UserRepository;
import com.knu.finance_tracer.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsApiController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public AnalyticsApiController(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    private String getEmailFromAuth(Authentication authentication) {
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        }
        return authentication.getName();
    }

    @GetMapping("/expenses-by-category")
    public List<CategoryExpenseDto> getExpensesByCategory(Authentication authentication) {
        String email = getEmailFromAuth(authentication);
        if (email == null) {
            throw new RuntimeException("Користувача не авторизовано");
        }
        User user = userRepository.findByEmail(email).orElseThrow();
        return transactionRepository.getExpensesGroupedByCategory(user.getId());
    }
}
