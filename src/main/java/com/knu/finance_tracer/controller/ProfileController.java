package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.repository.UserRepository;
import com.knu.finance_tracer.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    private String getEmailFromAuth(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser().getEmail();
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        }
        return authentication.getName();
    }

    @GetMapping("/profile")
    public String getProfile(Authentication authentication, Model model) {
        String email = getEmailFromAuth(authentication);
        if (email == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(email).orElseThrow();
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/generate-telegram-link")
    public String generateTelegramLink(Authentication authentication) {
        String email = getEmailFromAuth(authentication);
        if (email == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(email).orElseThrow();

        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setTelegramVerificationToken(token);
        userRepository.save(user);

        return "redirect:/profile";
    }
}
