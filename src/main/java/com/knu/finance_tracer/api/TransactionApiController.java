package com.knu.finance_tracer.api;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions API", description = "Управління транзакціями користувача")
public class TransactionApiController {

    private final TransactionService transactionService;
    private final SecurityUtil securityUtil;

    public TransactionApiController(TransactionService transactionService, SecurityUtil securityUtil) {
        this.transactionService = transactionService;
        this.securityUtil = securityUtil;
    }

    private void checkTransactionOwnership(Transaction transaction, User currentUser) {
        if (currentUser.getRole() != Role.ROLE_ADMIN
                && !transaction.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    @Operation(summary = "Отримати сторінку транзакцій", description = "Повертає транзакції з пагінацією")
    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("dateTime").descending());
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        Page<Transaction> transactions = currentUser.getRole() == Role.ROLE_ADMIN
                ? transactionService.getAllTransactionsForAdmin(pageable)
                : transactionService.getTransactions(currentUser.getId(), pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Отримати інформацію про конкретну транзакцію", description = "Повертає одину транзакцію")
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        checkTransactionOwnership(transaction, securityUtil.getCurrentAuthenticatedUser());
        return ResponseEntity.ok(transaction);
    }

    @Operation(summary = "Створити нову транзакцію")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createTransaction(@ModelAttribute TransactionCreateDto dto) {
        transactionService.createTransaction(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Змінити наявну транзакцію", description = "Повертає оновлену транзакцію")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateTransaction(@PathVariable Long id, @ModelAttribute TransactionCreateDto dto) {
        Transaction transaction = transactionService.getTransactionById(id);
        checkTransactionOwnership(transaction, securityUtil.getCurrentAuthenticatedUser());
        transactionService.updateTransaction(id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Видалити конкретну транзакцію")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        checkTransactionOwnership(transaction, securityUtil.getCurrentAuthenticatedUser());
        transactionService.deleteTransactionById(id);
        return ResponseEntity.noContent().build();
    }
}
