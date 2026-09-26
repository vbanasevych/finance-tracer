package com.knu.finance_tracer.api;

import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories API", description = "Управління категоріями витрат/доходів користувача")
public class CategoryApiController {

    private final CategoryService categoryService;
    private final UserService userService;
    private final Long CURRENT_USER_ID = 1L;

    public CategoryApiController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @Operation(summary = "Отримати сторінку категорій", description = "Повертає категорії з пагінацією (тільки активні)")
    @GetMapping
    public ResponseEntity<Page<Category>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(categoryService.getCategories(CURRENT_USER_ID, PageRequest.of(page, size)));
    }

    @Operation(summary = "Отримати інформацію про конкретну категорію", description = "Повертає одну категорію")
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Створити нову категорію")
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        category.setUser(userService.getUserById(CURRENT_USER_ID));
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Змінити наявну категорію", description = "Повертає оновлену категорію")
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDetails));
    }

    @Operation(summary = "Видалити конкретну категорію")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
