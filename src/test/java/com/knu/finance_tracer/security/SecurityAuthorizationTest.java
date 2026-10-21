package com.knu.finance_tracer.security;

import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.controller.ControllerTestConfig;
import com.knu.finance_tracer.controller.TransactionController;
import com.knu.finance_tracer.controller.UserController;
import com.knu.finance_tracer.service.TransactionService;
import com.knu.finance_tracer.service.UserService;
import com.knu.finance_tracer.service.AccountService;
import com.knu.finance_tracer.service.BudgetService;
import com.knu.finance_tracer.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TransactionController.class, UserController.class})
@AutoConfigureMockMvc
@Import(ControllerTestConfig.class)
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private BudgetService budgetService;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private SecurityUtil securityUtil;

    @Test
    @WithMockUser(username = "userA", roles = {"USER"})
    void userA_tryingToDeleteUserBTransaction_shouldReturn403Forbidden() throws Exception {
        User userB = new User();
        userB.setId(2L);
        userB.setRole(Role.ROLE_USER);

        Transaction transactionB = new Transaction();
        transactionB.setId(10L);
        transactionB.setUser(userB);

        User userA = new User();
        userA.setId(1L);
        userA.setRole(Role.ROLE_USER);

        when(transactionService.getTransactionById(10L)).thenReturn(transactionB);
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(userA);

        // Спроба юзера А видалити транзакцію юзера B має завершитися 403 Forbidden
        mockMvc.perform(post("/transactions/delete/10")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "userA", roles = {"USER"})
    void regularUser_tryingToAccessAdminUsersPanel_shouldReturn403Forbidden() throws Exception {
        User userA = new User();
        userA.setId(1L);
        userA.setRole(Role.ROLE_USER);

        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(userA);

        // Звичайний користувач не має доступу до панелі управління користувачами (/users)
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }
}
