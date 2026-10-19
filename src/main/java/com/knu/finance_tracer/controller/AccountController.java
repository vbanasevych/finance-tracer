package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.AccountService;
import com.knu.finance_tracer.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final SecurityUtil securityUtil;

    public AccountController(AccountService accountService, SecurityUtil securityUtil) {
        this.accountService = accountService;
        this.securityUtil = securityUtil;
    }

    @GetMapping
    public String listAccounts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        model.addAttribute("page", accountService.getAccounts(currentUser.getId(), pageable));
        return "accounts/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("account", new Account());
        return "accounts/create";
    }

    @PostMapping
    public String createAccount(@ModelAttribute Account account) {
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        account.setUser(currentUser);
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
