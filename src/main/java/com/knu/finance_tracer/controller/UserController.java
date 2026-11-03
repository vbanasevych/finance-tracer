package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.CustomUserDetails;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final SecurityUtil securityUtil;

    public UserController(UserService userService, SecurityUtil securityUtil) {
        this.userService = userService;
        this.securityUtil = securityUtil;
    }

    private void checkAdminAccess() {
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ дозволено лише адміністраторам");
        }
    }

    @GetMapping
    public String listUsers(Model model) {
        checkAdminAccess();
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", Role.values());
        return "users/list";
    }

    @PostMapping("/{id}/role")
    public String changeUserRole(@PathVariable Long id, @RequestParam Role role) {
        checkAdminAccess();
        userService.updateUserRole(id, role);
        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        checkAdminAccess();
        userService.deleteUser(id);
        return "redirect:/users";
    }

    @PostMapping("/profile/generate-telegram-link")
    public String generateTelegramLink(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.getUserById(userDetails.getUser().getId());

        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setTelegramVerificationToken(token);
        userService.createUser(user);

        model.addAttribute("telegramToken", token);
        return "profile";
    }
}
