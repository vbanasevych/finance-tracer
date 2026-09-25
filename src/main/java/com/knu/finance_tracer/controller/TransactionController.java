package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.repository.TransactionRepository;
import com.knu.finance_tracer.service.AccountService;
import com.knu.finance_tracer.service.BudgetService;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;

    public TransactionController(TransactionService transactionService,
                                 AccountService accountService,
                                 CategoryService categoryService,
                                 BudgetService budgetService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.budgetService = budgetService;
    }

    @GetMapping
    public String listTransactions(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   Model model) {
        Pageable pageable = PageRequest.of(
                page, size, Sort.by("dateTime").descending());

        Page<Transaction> transactionPage = transactionService.getTransactions(1L, pageable);

        List<Transaction> allTransactions = transactionService.getTransactionsByUserId(1L);
        List<String> warnings = budgetService.getBudgetWarnings(1L, allTransactions);

        model.addAttribute("page", transactionPage);
        model.addAttribute("budgetWarnings", warnings);
        return "transactions/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("transaction", new TransactionCreateDto());
        model.addAttribute("accounts", accountService.getAccountsByUserId(1L));
        model.addAttribute("categories", categoryService.getCategoriesByUserId(1L));
        return "transactions/create";
    }

    @PostMapping
    public String createTransaction(@ModelAttribute TransactionCreateDto dto) {
        transactionService.createTransaction(dto);
        return "redirect:/transactions/new?success";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);

        TransactionCreateDto dto = new TransactionCreateDto();
        dto.setAmount(transaction.getAmount());
        dto.setDateTime(transaction.getDateTime());
        dto.setDescription(transaction.getDescription());
        dto.setAccountId(transaction.getAccount().getId());
        dto.setCategoryId(transaction.getCategory().getId());

        model.addAttribute("accounts", accountService.getAccountsByUserId(1L));
        model.addAttribute("categories", categoryService.getCategoriesByUserId(1L));
        model.addAttribute("transactionDto", dto);
        model.addAttribute("transactionId", id);
        model.addAttribute("currentFileUrl", transaction.getReceiptFileUrl());
        return "transactions/edit";
    }

    @PostMapping("/update/{id}")
    public String updateTransaction(@PathVariable Long id, @ModelAttribute TransactionCreateDto dto) {
        transactionService.updateTransaction(id, dto);
        return "redirect:/transactions";
    }

    @PostMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransactionById(id);
        return "redirect:/transactions";
    }
}
