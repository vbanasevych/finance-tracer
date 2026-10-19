package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.BudgetService;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public String listBudgets(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        model.addAttribute("page", budgetService.getBudgets(currentUser.getId(), pageable));
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
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        model.addAttribute("budget", budgetService.getBudgetById(id));
        model.addAttribute("categories", categoryService.getCategoriesByUserId(currentUser.getId()));
        return "budgets/edit";
    }

    @PostMapping("/update/{id}")
    public String updateBudget(@PathVariable Long id, @ModelAttribute Budget budgetDetails) {
        budgetService.updateBudget(id, budgetDetails);
        return "redirect:/budgets";
    }

    @PostMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return "redirect:/budgets";
    }
}