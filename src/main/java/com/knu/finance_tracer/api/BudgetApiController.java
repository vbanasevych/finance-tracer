package com.knu.finance_tracer.api;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budgets API", description = "Управління лімітами користувача")
public class BudgetApiController {

    private final BudgetService budgetService;
    private final SecurityUtil securityUtil;

    public BudgetApiController(BudgetService budgetService, SecurityUtil securityUtil) {
        this.budgetService = budgetService;
        this.securityUtil = securityUtil;
    }

    private void checkBudgetOwnership(Budget budget, User currentUser) {
        if (currentUser.getRole() != Role.ROLE_ADMIN
                && !budget.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    @Operation(summary = "Отримати сторінку лімітів", description = "Повертає ліміти з пагінацією")
    @GetMapping
    public ResponseEntity<Page<Budget>> getBudgets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        Page<Budget> budgets = currentUser.getRole() == Role.ROLE_ADMIN
                ? budgetService.getAllBudgetsForAdmin(PageRequest.of(page, size))
                : budgetService.getBudgets(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(budgets);
    }

    @Operation(summary = "Отримати інформацію про конкретний ліміт", description = "Повертає один ліміт")
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        Budget budget = budgetService.getBudgetById(id);
        checkBudgetOwnership(budget, securityUtil.getCurrentAuthenticatedUser());
        return ResponseEntity.ok(budget);
    }

    @Operation(summary = "Створити новий ліміт")
    @PostMapping
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        budget.setUser(securityUtil.getCurrentAuthenticatedUser());
        Budget created = budgetService.createBudget(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Змінити наявний ліміт", description = "Повертає оновлений ліміт")
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget budgetDetails) {
        Budget budget = budgetService.getBudgetById(id);
        checkBudgetOwnership(budget, securityUtil.getCurrentAuthenticatedUser());
        return ResponseEntity.ok(budgetService.updateBudget(id, budgetDetails));
    }

    @Operation(summary = "Видалити конкретний ліміт")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        Budget budget = budgetService.getBudgetById(id);
        checkBudgetOwnership(budget, securityUtil.getCurrentAuthenticatedUser());
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
