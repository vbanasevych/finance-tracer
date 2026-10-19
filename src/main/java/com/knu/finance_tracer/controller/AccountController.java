package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final SecurityUtil securityUtil;

    public AccountController(AccountService accountService, SecurityUtil securityUtil) {
        this.accountService = accountService;
        this.securityUtil = securityUtil;
    }

    private void checkAccountOwnership(Account account, User currentUser) {
        if (currentUser.getRole() != Role.ROLE_ADMIN && !account.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ви не маєте доступу до цього рахунку");
        }
    }

    @GetMapping
    public String listAccounts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();
        Page<Account> accoutPage;

        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            accoutPage = accountService.getAllAccountsForAdmin(pageable);
        } else {
            accoutPage = accountService.getAccounts(currentUser.getId(), pageable);
        }

        model.addAttribute("page", accoutPage);
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
        Account account = accountService.getAccountById(id);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();

        checkAccountOwnership(account, currentUser);

        model.addAttribute("account", account);
        return "accounts/edit";
    }

    @PostMapping("/update/{id}")
    public String updateAccount(@PathVariable Long id, @ModelAttribute Account accountDetails) {
        Account account = accountService.getAccountById(id);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();

        checkAccountOwnership(account, currentUser);

        accountService.updateAccount(id, accountDetails);
        return "redirect:/accounts";
    }

    @PostMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();

        checkAccountOwnership(account, currentUser);

        accountService.deleteAccount(id);
        return "redirect:/accounts";
    }
}
