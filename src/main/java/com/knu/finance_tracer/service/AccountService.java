package com.knu.finance_tracer.service;

import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рахунок не знайдено"));
    }

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findAllByUserIdAndIsDeletedFalse(userId);
    }

    @Transactional
    public Account updateAccount(Long id, Account accountDetails) {
        Account account = getAccountById(id);
        account.setName(accountDetails.getName());
        account.setBalance(accountDetails.getBalance());
        account.setCurrency(accountDetails.getCurrency());
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account account = getAccountById(id);
        account.setIsDeleted(true);
        accountRepository.save(account);
    }
}
