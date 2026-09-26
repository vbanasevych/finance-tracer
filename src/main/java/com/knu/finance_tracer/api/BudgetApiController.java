package com.knu.finance_tracer.api;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.service.BudgetService;
import com.knu.finance_tracer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budgets API", description = "Управління лімітами користувача")
public class BudgetApiController {

    private final BudgetService budgetService;
    private final UserService userService;
    private final Long CURRENT_USER_ID = 1L;

    public BudgetApiController(BudgetService budgetService, UserService userService) {
        this.budgetService = budgetService;
        this.userService = userService;
    }

    @Operation(summary = "Отримати сторінку лімітів", description = "Повертає ліміти з пагінацією")
    @GetMapping
    public ResponseEntity<Page<Budget>> getBudgets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(budgetService.getBudgets(CURRENT_USER_ID, PageRequest.of(page, size)));
    }

    @Operation(summary = "Отримати інформацію про конкретний ліміт", description = "Повертає один ліміт")
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    @Operation(summary = "Створити новий ліміт")
    @PostMapping
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        budget.setUser(userService.getUserById(CURRENT_USER_ID));
        Budget created = budgetService.createBudget(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Змінити наявний ліміт", description = "Повертає оновлений ліміт")
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget budgetDetails) {
        return ResponseEntity.ok(budgetService.updateBudget(id, budgetDetails));
    }

    @Operation(summary = "Видалити конкретний ліміт")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
