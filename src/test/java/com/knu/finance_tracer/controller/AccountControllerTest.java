package com.knu.finance_tracer.controller;

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
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock private AccountService accountService;
    @Mock private UserService userService;
    @Mock private Model model;

    @InjectMocks
    private AccountController accountController;

    @Test
    void listAccounts_ShouldReturnViewAndModel() {
        Page<Account> page = new PageImpl<>(List.of(new Account()));
        when(accountService.getAccounts(eq(1L), any(Pageable.class))).thenReturn(page);
        String view = accountController.listAccounts(0, 5, model);
        assertEquals("accounts/list", view);
        verify(model).addAttribute(eq("page"), any());
    }

    @Test
    void showCreateForm_ShouldReturnView() {
        String view = accountController.showCreateForm(model);
        assertEquals("accounts/create", view);
        verify(model).addAttribute(eq("account"), any(Account.class));
    }

    @Test
    void createAccount_ShouldSaveAndRedirect() {
        Account account = new Account();
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);

        String view = accountController.createAccount(account);

        assertEquals("redirect:/accounts", view);
        assertEquals(user, account.getUser());
        verify(accountService).createAccount(account);
    }

    @Test
    void showEditForm_ShouldReturnViewAndModel() {
        when(accountService.getAccountById(1L)).thenReturn(new Account());
        String view = accountController.showEditForm(1L, model);
        assertEquals("accounts/edit", view);
        verify(model).addAttribute(eq("account"), any(Account.class));
    }

    @Test
    void updateAccount_ShouldUpdateAndRedirect() {
        String view = accountController.updateAccount(1L, new Account());
        assertEquals("redirect:/accounts", view);
        verify(accountService).updateAccount(eq(1L), any(Account.class));
    }

    @Test
    void deleteAccount_ShouldDeleteAndRedirect() {
        String view = accountController.deleteAccount(1L);
        assertEquals("redirect:/accounts", view);
        verify(accountService).deleteAccount(1L);
    }
}
