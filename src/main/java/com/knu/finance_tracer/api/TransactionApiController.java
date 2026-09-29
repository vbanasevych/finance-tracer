package com.knu.finance_tracer.api;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.Transaction;
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

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions API", description = "Управління транзакціями користувача")
public class TransactionApiController {

    private final TransactionService transactionService;
    private final Long CURRENT_USER_ID = 1L;

    public TransactionApiController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Отримати сторінку транзакцій", description = "Повертає транзакції з пагінацією")
    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("dateTime").descending());
        return ResponseEntity.ok(transactionService.getTransactions(CURRENT_USER_ID, pageable));
    }

    @Operation(summary = "Отримати інформацію про конкретну транзакцію", description = "Повертає одину транзакцію")
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
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
        transactionService.updateTransaction(id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Видалити конкретну транзакцію")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransactionById(id);
        return ResponseEntity.noContent().build();
    }
}
