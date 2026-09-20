package com.knu.finance_tracer.service;

import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_ShouldReturnSavedAccount() {
        Account account = new Account();
        when(accountRepository.save(any())).thenReturn(account);

        Account saved = accountService.createAccount(new Account());

        assertNotNull(saved);
        verify(accountRepository).save(any());
    }

    @Test
    void getAccountById_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));
        assertNotNull(accountService.getAccountById(1L));
    }

    @Test
    void getAccountById_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountById(1L));
    }

    @Test
    void getAccountsByUserId_ShouldReturnList() {
        when(accountRepository.findAllByUserIdAndIsDeletedFalse(1L)).thenReturn(List.of(new Account()));
        assertEquals(1, accountService.getAccountsByUserId(1L).size());
    }

    @Test
    void updateAccount_ShouldUpdateFields() {
        Account existing = new Account();
        Account details = new Account();
        details.setName("Monobank");
        details.setBalance(BigDecimal.valueOf(100));
        details.setCurrency("UAH");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any())).thenReturn(existing);

        accountService.updateAccount(1L, details);

        assertEquals("Monobank", existing.getName());
        assertEquals(BigDecimal.valueOf(100), existing.getBalance());
        assertEquals("UAH", existing.getCurrency());
    }

    @Test
    void deleteAccount_Success() {
        Account account = new Account();
        account.setIsDeleted(false);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.deleteAccount(1L);

        assertTrue(account.getIsDeleted());
        verify(accountRepository).save(account);
    }
}
