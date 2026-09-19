package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.service.AccountService;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock private TransactionService transactionService;
    @Mock private AccountService accountService;
    @Mock private CategoryService categoryService;
    @Mock private Model model;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void listTransactions_ShouldReturnView() {
        when(transactionService.getTransactionsByUserId(1L)).thenReturn(List.of());
        String view = transactionController.listTransactions(model);
        assertEquals("transactions/list", view);
        verify(model).addAttribute(eq("transactions"), anyList());
    }

    @Test
    void showCreateForm_ShouldReturnView() {
        when(accountService.getAccountsByUserId(1L)).thenReturn(List.of());
        when(categoryService.getCategoriesByUserId(1L)).thenReturn(List.of());

        String view = transactionController.showCreateForm(model);

        assertEquals("transactions/create", view);
        verify(model).addAttribute(eq("transaction"), any(TransactionCreateDto.class));
        verify(model).addAttribute(eq("accounts"), anyList());
        verify(model).addAttribute(eq("categories"), anyList());
    }

    @Test
    void createTransaction_ShouldSaveAndRedirect() {
        String view = transactionController.createTransaction(new TransactionCreateDto());
        assertEquals("redirect:/transactions/new?success", view);
        verify(transactionService).createTransaction(any(TransactionCreateDto.class));
    }

    @Test
    void showEditForm_ShouldMapDtoAndReturnView() {
        Transaction t = new Transaction();
        t.setAmount(BigDecimal.TEN);
        t.setDateTime(LocalDateTime.now());
        t.setDescription("Test");
        t.setReceiptFileUrl("url");

        Account acc = new Account(); acc.setId(2L);
        Category cat = new Category(); cat.setId(3L);
        t.setAccount(acc);
        t.setCategory(cat);

        when(transactionService.getTransactionById(1L)).thenReturn(t);
        when(accountService.getAccountsByUserId(1L)).thenReturn(List.of());
        when(categoryService.getCategoriesByUserId(1L)).thenReturn(List.of());

        String view = transactionController.showEditForm(1L, model);

        assertEquals("transactions/edit", view);
        verify(model).addAttribute(eq("transactionDto"), any(TransactionCreateDto.class));
        verify(model).addAttribute("transactionId", 1L);
        verify(model).addAttribute("currentFileUrl", "url");
    }

    @Test
    void updateTransaction_ShouldUpdateAndRedirect() {
        String view = transactionController.updateTransaction(1L, new TransactionCreateDto());
        assertEquals("redirect:/transactions", view);
        verify(transactionService).updateTransaction(eq(1L), any(TransactionCreateDto.class));
    }

    @Test
    void deleteTransaction_ShouldDeleteAndRedirect() {
        String view = transactionController.deleteTransaction(1L);
        assertEquals("redirect:/transactions", view);
        verify(transactionService).deleteTransactionById(1L);
    }
}
