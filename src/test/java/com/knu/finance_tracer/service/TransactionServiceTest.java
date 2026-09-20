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

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_Expense_ShouldDecreaseBalance() {
        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAmount(BigDecimal.TEN);
        dto.setDateTime(LocalDateTime.now().minusMinutes(5));
        dto.setAccountId(1L);
        dto.setCategoryId(1L);

        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
        dto.setReceiptFile(file);

        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100));

        Category category = new Category();
        category.setIsExpense(true);

        when(s3Service.uploadFile(any())).thenReturn("http://s3-url.com/file.jpg");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        transactionService.createTransaction(dto);

        assertEquals(BigDecimal.valueOf(90), account.getBalance());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_WithNewReceiptFile_ShouldUploadToS3() {
        Transaction existing = new Transaction();
        existing.setAmount(BigDecimal.valueOf(50));
        existing.setDateTime(LocalDateTime.now().minusDays(1));

        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100));
        Category category = new Category();
        category.setIsExpense(true);

        existing.setAccount(account);
        existing.setCategory(category);

        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAmount(BigDecimal.valueOf(20));
        dto.setDateTime(LocalDateTime.now().minusMinutes(1));
        dto.setAccountId(1L);
        dto.setCategoryId(1L);

        MockMultipartFile mockFile =
                new MockMultipartFile(
                        "file", "receipt.jpg", "image/jpeg", "image content".getBytes());
        dto.setReceiptFile(mockFile);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        when(s3Service.uploadFile(mockFile)).thenReturn("https://aws-s3-url.com/receipt.jpg");

        transactionService.updateTransaction(1L, dto);

        assertEquals("https://aws-s3-url.com/receipt.jpg", existing.getReceiptFileUrl());
        verify(s3Service, times(1)).uploadFile(mockFile);
    }

    @Test
    void createTransaction_Income_ShouldIncreaseBalance() {
        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAmount(BigDecimal.TEN);
        dto.setDateTime(LocalDateTime.now().minusMinutes(5));
        dto.setAccountId(1L);
        dto.setCategoryId(1L);

        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100));

        Category category = new Category();
        category.setIsExpense(false);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        transactionService.createTransaction(dto);

        assertEquals(BigDecimal.valueOf(110), account.getBalance());
    }

    @Test
    void createTransaction_FutureDate_ShouldThrowException() {
        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setDateTime(LocalDateTime.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto));
    }

    @Test
    void updateTransaction_ShouldRevertOldAndApplyNewBalance() {
        Transaction existing = new Transaction();
        existing.setAmount(BigDecimal.valueOf(50));
        existing.setDateTime(LocalDateTime.now().minusDays(1));

        Account oldAccount = new Account();
        oldAccount.setBalance(BigDecimal.valueOf(100));
        Category oldCategory = new Category();
        oldCategory.setIsExpense(true);

        existing.setAccount(oldAccount);
        existing.setCategory(oldCategory);

        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAmount(BigDecimal.valueOf(20));
        dto.setDateTime(LocalDateTime.now().minusMinutes(1));
        dto.setAccountId(2L);
        dto.setCategoryId(2L);

        Account newAccount = new Account();
        newAccount.setBalance(BigDecimal.valueOf(200));
        Category newCategory = new Category();
        newCategory.setIsExpense(false);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(newAccount));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));

        transactionService.updateTransaction(1L, dto);

        assertEquals(BigDecimal.valueOf(150), oldAccount.getBalance());
        assertEquals(BigDecimal.valueOf(220), newAccount.getBalance());

        verify(accountRepository).save(oldAccount);
        verify(accountRepository).save(newAccount);
        verify(transactionRepository).save(existing);
    }

    @Test
    void updateTransaction_FutureDate_ShouldThrowException() {
        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setDateTime(LocalDateTime.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, () -> transactionService.updateTransaction(1L, dto));
    }

    @Test
    void deleteTransactionById_ShouldRevertBalance() {
        Transaction existing = new Transaction();
        existing.setAmount(BigDecimal.valueOf(30));

        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100));
        Category category = new Category();
        category.setIsExpense(true);

        existing.setAccount(account);
        existing.setCategory(category);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existing));

        transactionService.deleteTransactionById(1L);

        assertEquals(BigDecimal.valueOf(130), account.getBalance());
        verify(accountRepository).save(account);
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void getTransactionsByUserId_Success() {
        when(transactionRepository.findAllByUserId(1L)).thenReturn(List.of(new Transaction()));
        assertEquals(1, transactionService.getTransactionsByUserId(1L).size());
    }

    @Test
    void getTransactionById_NotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionById(1L));
    }
}
