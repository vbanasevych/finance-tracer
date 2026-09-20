package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.service.AccountService;
import com.knu.finance_tracer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final Long CURRENT_USER_ID = 1L;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping
    public String listAccounts(Model model) {
        model.addAttribute("accounts", accountService.getAccountsByUserId(CURRENT_USER_ID));
        return "accounts/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("account", new Account());
        return "accounts/create";
    }

    @PostMapping
    public String createAccount(@ModelAttribute Account account) {
        User user = userService.getUserById(CURRENT_USER_ID);
        account.setUser(user);
        accountService.createAccount(account);
        return "redirect:/accounts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("account", accountService.getAccountById(id));
        return "accounts/edit";
    }

    @PostMapping("/update/{id}")
    public String updateAccount(@PathVariable Long id, @ModelAttribute Account accountDetails) {
        accountService.updateAccount(id, accountDetails);
        return "redirect:/accounts";
    }

    @PostMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return "redirect:/accounts";
    }
}
