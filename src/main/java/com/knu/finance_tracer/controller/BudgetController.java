package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.BudgetService;
import com.knu.finance_tracer.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final SecurityUtil securityUtil;

    public BudgetController(BudgetService budgetService, CategoryService categoryService, SecurityUtil securityUtil) {
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.securityUtil = securityUtil;
    }

    private void checkBudgetOwnership(Budget budget, User currentUser) {
        if (currentUser.getRole() != Role.ROLE_ADMIN && !budget.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ви не маєте доступу до цього бюджету");
        }
    }

    @GetMapping
    public String listBudgets(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        Page<Budget> budgetPage;

        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            budgetPage = budgetService.getAllBudgetsForAdmin(pageable);
        } else {
            budgetPage = budgetService.getBudgets(currentUser.getId(), pageable);
        }

        model.addAttribute("page", budgetPage);
        return "budgets/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        model.addAttribute("budget", new Budget());
        model.addAttribute("categories", categoryService.getCategoriesByUserId(currentUser.getId()));
        return "budgets/create";
    }

    @PostMapping
    public String createBudget(@ModelAttribute Budget budget) {
        User user = securityUtil.getCurrentAuthenticatedUser();
        budget.setUser(user);
        budgetService.createBudget(budget);
        return "redirect:/budgets";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Budget budget = budgetService.getBudgetById(id);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();

        checkBudgetOwnership(budget, currentUser);

        model.addAttribute("budget", budget);
        model.addAttribute("categories", categoryService.getCategoriesByUserId(budget.getUser().getId()));
        return "budgets/edit";
    }

    @PostMapping("/update/{id}")
    public String updateBudget(@PathVariable Long id, @ModelAttribute Budget budgetDetails) {
        Budget budget = budgetService.getBudgetById(id);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();

        checkBudgetOwnership(budget, currentUser);

        budgetService.updateBudget(id, budgetDetails);
        return "redirect:/budgets";
    }

    @PostMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id) {
        Budget budget = budgetService.getBudgetById(id);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();

        checkBudgetOwnership(budget, currentUser);

        budgetService.deleteBudget(id);
        return "redirect:/budgets";
    }
}
