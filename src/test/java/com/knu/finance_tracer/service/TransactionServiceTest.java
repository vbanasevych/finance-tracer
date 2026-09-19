package com.knu.finance_tracer.service;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.repository.AccountRepository;
import com.knu.finance_tracer.repository.CategoryRepository;
import com.knu.finance_tracer.repository.TransactionRepository;
import com.knu.finance_tracer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_Success() {
        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAmount(BigDecimal.TEN);
        dto.setAccountId(1L);
        dto.setCategoryId(1L);
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
        dto.setReceiptFile(file);

        when(s3Service.uploadFile(any())).thenReturn("http://s3-url.com/file.jpg");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        transactionService.createTransaction(dto);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getTransactionsByUserId_Success() {
        when(transactionRepository.findAllByUserId(1L)).thenReturn(List.of(new Transaction()));
        assertEquals(1, transactionService.getTransactionsByUserId(1L).size());
    }

    @Test
    void getTransactionById_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(new Transaction()));
        assertNotNull(transactionService.getTransactionById(1L));
    }

    @Test
    void getTransactionById_NotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionById(1L));
    }

    @Test
    void updateTransaction_WithNewFile_Success() {
        Transaction existing = new Transaction();
        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAccountId(1L);
        dto.setCategoryId(1L);
        dto.setReceiptFile(new MockMultipartFile("file", "new.jpg", "image/jpeg", "data".getBytes()));

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(s3Service.uploadFile(any())).thenReturn("http://s3-url.com/new.jpg");

        transactionService.updateTransaction(1L, dto);

        assertEquals("http://s3-url.com/new.jpg", existing.getReceiptFileUrl());
        verify(transactionRepository).save(existing);
    }

    @Test
    void updateTransaction_WithoutNewFile_Success() {
        Transaction existing = new Transaction();
        existing.setReceiptFileUrl("old-url");

        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAccountId(1L);
        dto.setCategoryId(1L);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));

        transactionService.updateTransaction(1L, dto);

        assertEquals("old-url", existing.getReceiptFileUrl());
        verify(s3Service, never()).uploadFile(any());
        verify(transactionRepository).save(existing);
    }

    @Test
    void deleteTransactionById_Success() {
        transactionService.deleteTransactionById(1L);
        verify(transactionRepository).deleteById(1L);
    }
}
