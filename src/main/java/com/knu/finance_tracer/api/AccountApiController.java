package com.knu.finance_tracer.api;

import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.service.AccountService;
import com.knu.finance_tracer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts API", description = "Управління фінансовими рахунками користувача")
public class AccountApiController {

    private final AccountService accountService;
    private final UserService userService;
    private final Long CURRENT_USER_ID = 1L;

    public AccountApiController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @Operation(summary = "Отримати сторінку рахунків", description = "Повертає рахунки з пагінацією (тільки активні)")
    @GetMapping
    public ResponseEntity<Page<Account>> getAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(accountService.getAccounts(CURRENT_USER_ID, PageRequest.of(page, size)));
    }

    @Operation(summary = "Отримати інформацію про конкретний рахунок", description = "Повертає один рахунок")
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @Operation(summary = "Створити новий рахунок")
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        account.setUser(userService.getUserById(CURRENT_USER_ID));
        Account created = accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Змінити наявний рахунок", description = "Повертає оновлений рахунок")
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account accountDetails) {
        return ResponseEntity.ok(accountService.updateAccount(id, accountDetails));
    }

    @Operation(summary = "Видалити конкретний рахунок")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
