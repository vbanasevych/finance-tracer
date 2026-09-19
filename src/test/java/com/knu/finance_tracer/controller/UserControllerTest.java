package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserService userService;
    @Mock private Model model;

    @InjectMocks
    private UserController userController;

    @Test
    void listUsers_ShouldReturnView() {
        when(userService.getAllUsers()).thenReturn(List.of(new User()));
        String view = userController.listUsers(model);
        assertEquals("users/list", view);
        verify(model).addAttribute(eq("users"), anyList());
    }

    @Test
    void showCreateForm_ShouldReturnView() {
        String view = userController.showCreateForm(model);
        assertEquals("users/create", view);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void createUser_ShouldSaveAndRedirect() {
        String view = userController.createUser(new User());
        assertEquals("redirect:/users", view);
        verify(userService).createUser(any(User.class));
    }

    @Test
    void showEditForm_ShouldReturnView() {
        when(userService.getUserById(1L)).thenReturn(new User());
        String view = userController.showEditForm(1L, model);
        assertEquals("users/edit", view);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateAndRedirect() {
        String view = userController.updateUser(1L, new User());
        assertEquals("redirect:/users", view);
        verify(userService).updateUser(eq(1L), any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteAndRedirect() {
        String view = userController.deleteUser(1L);
        assertEquals("redirect:/users", view);
        verify(userService).deleteUser(1L);
    }
}
