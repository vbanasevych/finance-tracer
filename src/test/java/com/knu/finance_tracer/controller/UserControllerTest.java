package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(ControllerTestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private UserService userService;
    @MockitoBean private SecurityUtil securityUtil;
    @MockitoBean private CacheManager cacheManager;

    @Test
    @WithMockUser
    void listUsers_asAdmin() throws Exception {
        User admin = new User(); admin.setRole(Role.ROLE_ADMIN);
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(admin);
        when(userService.getAllUsers()).thenReturn(List.of(admin));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/list"));
    }

    @Test
    @WithMockUser
    void listUsers_asRegularUser_shouldBeForbidden() throws Exception {
        User regular = new User(); regular.setRole(Role.ROLE_USER);
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(regular);

        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void changeUserRole() throws Exception {
        User admin = new User(); admin.setRole(Role.ROLE_ADMIN);
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(admin);

        mockMvc.perform(post("/users/1/role").with(csrf()).param("role", "ROLE_ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @WithMockUser
    void deleteUser() throws Exception {
        User admin = new User(); admin.setRole(Role.ROLE_ADMIN);
        when(securityUtil.getCurrentAuthenticatedUser()).thenReturn(admin);

        mockMvc.perform(post("/users/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }
}
