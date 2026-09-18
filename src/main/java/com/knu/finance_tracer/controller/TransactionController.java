package com.knu.finance_tracer.controller;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.repository.TransactionRepository;
import com.knu.finance_tracer.service.AccountService;
import com.knu.finance_tracer.service.CategoryService;
import com.knu.finance_tracer.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;

    public TransactionController(TransactionService transactionService, AccountService accountService,  CategoryService categoryService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listTransactions(Model model) {
        model.addAttribute("transactions", transactionService.getTransactionsByUserId(1L));
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
