package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.security.SecurityUtil;
import com.knu.finance_tracer.service.AccountService;
import com.knu.finance_tracer.service.BudgetService;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private final SecurityUtil securityUtil;

    public TransactionController(TransactionService transactionService,
                                 AccountService accountService,
                                 CategoryService categoryService,
                                 BudgetService budgetService,
                                 SecurityUtil securityUtil) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.budgetService = budgetService;
        this.securityUtil = securityUtil;
    }

    private void checkTransactionOwnership(Transaction transaction, User currentUser) {
        if (currentUser.getRole() != Role.ROLE_ADMIN && !transaction.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ви не маєте доступу до цієї транзакції");
        }
    }

    @GetMapping
    public String listTransactions(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateTime").descending());
        User currentUser = securityUtil.getCurrentAuthenticatedUser();

        Page<Transaction> transactionPage;
        List<String> warnings = new ArrayList<>();

        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            transactionPage = transactionService.getAllTransactionsForAdmin(pageable);
        } else {
            transactionPage = transactionService.getTransactions(currentUser.getId(), pageable);
            List<Transaction> allTransactions = transactionService.getTransactionsByUserId(currentUser.getId());
            warnings = budgetService.getBudgetWarnings(currentUser.getId(), allTransactions);
        }

        model.addAttribute("page", transactionPage);
        model.addAttribute("budgetWarnings", warnings);
        return "transactions/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Long userId = securityUtil.getCurrentAuthenticatedUser().getId();

        model.addAttribute("transaction", new TransactionCreateDto());
        model.addAttribute("accounts", accountService.getAccountsByUserId(userId));
        model.addAttribute("categories", categoryService.getCategoriesByUserId(userId));

        return "transactions/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();

        checkTransactionOwnership(transaction, currentUser);

        Long ownerId = transaction.getUser().getId();

        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAmount(transaction.getAmount());
        dto.setDateTime(transaction.getDateTime());
        dto.setDescription(transaction.getDescription());
        dto.setAccountId(transaction.getAccount().getId());
        dto.setCategoryId(transaction.getCategory().getId());

        model.addAttribute("accounts", accountService.getAccountsByUserId(ownerId));
        model.addAttribute("categories", categoryService.getCategoriesByUserId(ownerId));
        model.addAttribute("transactionDto", dto);
        model.addAttribute("transactionId", id);
        model.addAttribute("currentFileUrl", transaction.getReceiptFileUrl());
        return "transactions/edit";
    }

    @PostMapping("/update/{id}")
    public String updateTransaction(@PathVariable Long id, @ModelAttribute TransactionCreateDto dto) {
        Transaction transaction = transactionService.getTransactionById(id);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();

        checkTransactionOwnership(transaction, currentUser);

        transactionService.updateTransaction(id, dto);
        return "redirect:/transactions";
    }

    @PostMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        User currentUser = securityUtil.getCurrentAuthenticatedUser();

        checkTransactionOwnership(transaction, currentUser);

        transactionService.deleteTransactionById(id);
        return "redirect:/transactions";
    }

    @PostMapping
    public String createTransaction(@ModelAttribute TransactionCreateDto dto) {
        transactionService.createTransaction(dto);
        return "redirect:/transactions/new?success";
    }
}
