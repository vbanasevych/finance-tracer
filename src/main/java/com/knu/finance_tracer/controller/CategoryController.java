package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
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
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityUtil securityUtil;

    public CategoryController(CategoryService categoryService, SecurityUtil securityUtil) {
        this.categoryService = categoryService;
        this.securityUtil = securityUtil;
    }

    private void checkCategoryOwnership(Category category, User currentUser) {
        if (currentUser.getRole() != Role.ROLE_ADMIN && !category.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ви не можете редагувати базові категорії");
        }
    }

    @GetMapping
    public String listCategories(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, size);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        Page<Category> categoryPage;

        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            categoryPage = categoryService.getAllCategoriesForAdmin(pageable);
        } else {
            categoryPage = categoryService.getCategories(currentUser.getId(), pageable);
        }

        model.addAttribute("page", categoryPage);
        model.addAttribute("currentUser", currentUser);
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        if (currentUser.getRole() == Role.ROLE_USER) {
            return "redirect:/categories";
        }
        model.addAttribute("category", new Category());
        return "categories/create";
    }

    @PostMapping
    public String createCategory(@ModelAttribute Category category) {
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        if (currentUser.getRole() == Role.ROLE_USER) {
            return "redirect:/categories";
        }
        category.setUser(currentUser);
        categoryService.createCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        Category category = categoryService.getCategoryById(id);

        checkCategoryOwnership(category, currentUser);

        model.addAttribute("category", category);
        return "categories/edit";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category categoryDetails) {
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        Category category = categoryService.getCategoryById(id);

        checkCategoryOwnership(category, currentUser);

        categoryService.updateCategory(id, categoryDetails);
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        Category category = categoryService.getCategoryById(id);

        checkCategoryOwnership(category, currentUser);

        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}
