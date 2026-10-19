package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole(Role.ROLE_USER);
        userService.createUser(user);
        return "redirect:/login?registered";
    }
}
