package com.knu.finance_tracer.service;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.*;
import com.knu.finance_tracer.repository.*;
import com.knu.finance_tracer.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private S3Service s3Service;
    @Mock private SecurityUtil securityUtil;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;
    private Account account;
    private Category category;
    private User user;
    private TransactionCreateDto dto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(1000));

        category = new Category();
        category.setId(1L);
        category.setIsExpense(true);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setDateTime(LocalDateTime.now().minusDays(1));
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setUser(user);

        dto = new TransactionCreateDto();
        dto.setAmount(BigDecimal.valueOf(200));
        dto.setDateTime(LocalDateTime.now().minusHours(1));
        dto.setDescription("Test");
        dto.setAccountId(1L);
        dto.setCategoryId(1L);
        dto.setReceiptFile(new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[10]));
    }

    @Test
    void createTransaction_shouldSuccess_whenValidData() {
        when(s3Service.uploadFile(any())).thenReturn("http://s3/file.jpg");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(user);

        transactionService.createTransaction(dto);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertEquals(0, BigDecimal.valueOf(800).compareTo(account.getBalance())); // 1000 - 200
    }

    @Test
    void createTransaction_shouldThrowException_whenDateInFuture() {
        dto.setDateTime(LocalDateTime.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto));
    }

    @Test
    void getTransactionsByUserId_shouldReturnList() {
        when(transactionRepository.findAllByUserId(1L)).thenReturn(List.of(transaction));

        List<Transaction> result = transactionService.getTransactionsByUserId(1L);

        assertFalse(result.isEmpty());
    }

    @Test
    void getTransactionById_shouldReturnTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Transaction found = transactionService.getTransactionById(1L);

        assertNotNull(found);
    }

    @Test
    void getTransactionById_shouldThrowException_whenNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionById(99L));
    }

    @Test
    void getTransactions_pageable_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of(transaction));
        when(transactionRepository.findAllByUserId(1L, pageable)).thenReturn(page);

        Page<Transaction> result = transactionService.getTransactions(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateTransaction_shouldUpdateSuccessfully() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        transactionService.updateTransaction(1L, dto);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void deleteTransactionById_shouldRevertBalanceAndDelete() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransactionById(1L);

        verify(transactionRepository, times(1)).deleteById(1L);
        assertEquals(0, BigDecimal.valueOf(1100).compareTo(account.getBalance())); // 1000 + 100 (revert expense)
    }

    @Test
    void getAllTransactionsForAdmin_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of(transaction));
        when(transactionRepository.findAll(pageable)).thenReturn(page);

        Page<Transaction> result = transactionService.getAllTransactionsForAdmin(pageable);

        assertNotNull(result);
    }
}
