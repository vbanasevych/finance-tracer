package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.AccountService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(ControllerTestConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private AccountService accountService;
    @MockitoBean
    private SecurityUtil securityUtil;
    @MockitoBean private CacheManager cacheManager;

    private User regularUser;
    private User adminUser;
    private Account account;

    @BeforeEach
    void setUp() {
        regularUser = new User();
        regularUser.setId(1L);
        regularUser.setRole(Role.ROLE_USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setRole(Role.ROLE_ADMIN);

        account = new Account();
        account.setId(1L);
        account.setUser(regularUser);
        account.setName("Mono");
        account.setBalance(BigDecimal.valueOf(100));
        account.setCurrency("UAH");
    }

    @Test
    @WithMockUser
    void listAccounts_asUser() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(accountService.getAccounts(eq(1L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(account)));

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts/list"));
    }

    @Test
    @WithMockUser
    void listAccounts_asAdmin() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(adminUser);
        when(accountService.getAllAccountsForAdmin(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(account)));

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts/list"));
    }

    @Test
    @WithMockUser
    void showCreateForm() throws Exception {
        mockMvc.perform(get("/accounts/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts/create"));
    }

    @Test
    @WithMockUser
    void createAccount() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);

        mockMvc.perform(post("/accounts")
                        .with(csrf())
                        .flashAttr("account", account))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts"));
    }

    @Test
    @WithMockUser
    void showEditForm_owner() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(accountService.getAccountById(1L)).thenReturn(account);

        mockMvc.perform(get("/accounts/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts/edit"));
    }

    @Test
    @WithMockUser
    void showEditForm_forbidden() throws Exception {
        User another = new User(); another.setId(99L); another.setRole(Role.ROLE_USER);
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(another);
        when(accountService.getAccountById(1L)).thenReturn(account);

        mockMvc.perform(get("/accounts/edit/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateAccount() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(accountService.getAccountById(1L)).thenReturn(account);

        mockMvc.perform(post("/accounts/update/1").with(csrf()).flashAttr("account", account))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts"));
    }

    @Test
    @WithMockUser
    void deleteAccount() throws Exception {
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);
        when(accountService.getAccountById(1L)).thenReturn(account);

        mockMvc.perform(post("/accounts/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts"));
    }
}
