package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.repository.UserRepository;
import com.knu.finance_tracer.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow();
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/generate-telegram-link")
    public String generateTelegramLink(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow();

        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setTelegramVerificationToken(token);
        userRepository.save(user);

        return "redirect:/profile";
    }
}
