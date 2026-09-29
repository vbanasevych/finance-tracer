package com.knu.finance_tracer.api;

import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.service.AccountService;
import com.knu.finance_tracer.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountApiControllerTest {

    @Mock private AccountService accountService;
    @Mock private UserService userService;

    @InjectMocks
    private AccountApiController controller;

    @Test
    void getAccounts_ShouldReturnPageAnd200() {
        Page<Account> page = new PageImpl<>(List.of(new Account()));
        when(accountService.getAccounts(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<Account>> response = controller.getAccounts(0, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void getAccountById_ShouldReturnAccountAnd200() {
        Account account = new Account();
        when(accountService.getAccountById(1L)).thenReturn(account);

        ResponseEntity<Account> response = controller.getAccountById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(account, response.getBody());
    }

    @Test
    void createAccount_ShouldReturnCreatedAccountAnd201() {
        Account account = new Account();
        when(userService.getUserById(1L)).thenReturn(new User());
        when(accountService.createAccount(any())).thenReturn(account);

        ResponseEntity<Account> response = controller.createAccount(new Account());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(account, response.getBody());
    }

    @Test
    void updateAccount_ShouldReturnUpdatedAccountAnd200() {
        Account account = new Account();
        when(accountService.updateAccount(eq(1L), any())).thenReturn(account);

        ResponseEntity<Account> response = controller.updateAccount(1L, new Account());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(account, response.getBody());
    }

    @Test
    void deleteAccount_ShouldReturn204() {
        ResponseEntity<Void> response = controller.deleteAccount(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(accountService).deleteAccount(1L);
    }
}
