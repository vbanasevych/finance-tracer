package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Budget;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.service.BudgetService;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final Long CURRENT_USER_ID = 1L;

    public BudgetController(BudgetService budgetService, CategoryService categoryService, UserService userService) {
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public String listBudgets(Model model) {
        model.addAttribute("budgets", budgetService.getBudgetsByUserId(CURRENT_USER_ID));
        return "budgets/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("budget", new Budget());
        model.addAttribute("categories", categoryService.getCategoriesByUserId(CURRENT_USER_ID));
        return "budgets/create";
    }

    @PostMapping
    public String createBudget(@ModelAttribute Budget budget) {
        User user = userService.getUserById(CURRENT_USER_ID);
        budget.setUser(user);
        budgetService.createBudget(budget);
        return "redirect:/budgets";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("budget", budgetService.getBudgetById(id));
        model.addAttribute("categories", categoryService.getCategoriesByUserId(CURRENT_USER_ID));
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