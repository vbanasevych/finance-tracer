package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.*;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(ControllerTestConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private TransactionService transactionService;
    @MockitoBean private AccountService accountService;
    @MockitoBean private CategoryService categoryService;
    @MockitoBean private BudgetService budgetService;
    @MockitoBean private SecurityUtil securityUtil;
    @MockitoBean private CacheManager cacheManager;

    private User regularUser;
    private User adminUser;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        regularUser = new User();
        regularUser.setId(1L);
        regularUser.setRole(Role.ROLE_USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setRole(Role.ROLE_ADMIN);

        Account account = new Account();
        account.setId(1L);

        Category category = new Category();
        category.setId(1L);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setUser(regularUser);
        transaction.setAccount(account);
        transaction.setCategory(category);
    }

    @Test
    @WithMockUser
    void listTransactions_asUser() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(transactionService.getTransactions(eq(1L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(transaction)));
        when(transactionService.getTransactionsByUserId(1L)).thenReturn(List.of(transaction));
        when(budgetService.getBudgetWarnings(eq(1L), any())).thenReturn(List.of("Warning"));

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/list"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @WithMockUser
    void listTransactions_asAdmin() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(adminUser);
        when(transactionService.getAllTransactionsForAdmin(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(transaction)));

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/list"));
    }

    @Test
    @WithMockUser
    void showCreateForm() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(accountService.getAccountsByUserId(1L)).thenReturn(List.of());
        when(categoryService.getCategoriesByUserId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/create"));
    }

    @Test
    @WithMockUser
    void showEditForm_owner() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(transactionService.getTransactionById(1L)).thenReturn(transaction);

        mockMvc.perform(get("/transactions/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/edit"));
    }

    @Test
    @WithMockUser
    void showEditForm_forbidden() throws Exception {
        User anotherUser = new User();
        anotherUser.setId(99L);
        anotherUser.setRole(Role.ROLE_USER);

        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(anotherUser);
        when(transactionService.getTransactionById(1L)).thenReturn(transaction);

        mockMvc.perform(get("/transactions/edit/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateTransaction() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(transactionService.getTransactionById(1L)).thenReturn(transaction);

        mockMvc.perform(post("/transactions/update/1")
                        .with(csrf())
                        .flashAttr("transactionCreateDto", new TransactionCreateDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"));
    }

    @Test
    @WithMockUser
    void deleteTransaction() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(transactionService.getTransactionById(1L)).thenReturn(transaction);

        mockMvc.perform(post("/transactions/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"));
    }

    @Test
    @WithMockUser
    void createTransaction() throws Exception {
        mockMvc.perform(post("/transactions")
                        .with(csrf())
                        .flashAttr("transactionCreateDto", new TransactionCreateDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions/new?success"));
    }
}
