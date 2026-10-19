package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityUtil securityUtil;

    public CategoryController(CategoryService categoryService, SecurityUtil securityUtil) {
        this.categoryService = categoryService;
        this.securityUtil = securityUtil;
    }

    @GetMapping
    public String listCategories(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, size);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        model.addAttribute("page", categoryService.getCategories(currentUser.getId(), pageable));
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/create";
    }

    @PostMapping
    public String createCategory(@ModelAttribute Category category) {
        User user = securityUtil.getCurrentAuthenticatedUser();
        category.setUser(user);
        categoryService.createCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "categories/edit";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category categoryDetails) {
        categoryService.updateCategory(id, categoryDetails);
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}