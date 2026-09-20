package com.knu.finance_tracer.service;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.repository.AccountRepository;
import com.knu.finance_tracer.repository.CategoryRepository;
import com.knu.finance_tracer.repository.TransactionRepository;
import com.knu.finance_tracer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository,
                              UserRepository userRepository,
                              S3Service s3Service) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    private void updateAccountBalance(Account account, Category category, BigDecimal amount, boolean isRevert) {
        BigDecimal adjustment = category.getIsExpense() ? amount.negate() : amount;

        if (isRevert) {
            adjustment = adjustment.negate();
        }

        account.setBalance(account.getBalance().add(adjustment));
        accountRepository.save(account);
    }

    @Transactional
    public void createTransaction(TransactionCreateDto dto) {
        if (dto.getDateTime().isAfter(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата транзакції не може бути у майбутньому");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setDateTime(dto.getDateTime());
        transaction.setDescription(dto.getDescription());

        String fileUrl = s3Service.uploadFile(dto.getReceiptFile());
        transaction.setReceiptFileUrl(fileUrl);

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Рахунок не знайдено"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Категорію не знайдено"));

        transaction.setAccount(account);
        transaction.setCategory(category);

        updateAccountBalance(account, category, dto.getAmount(), false);

        transaction.setUser(userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено")));

        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findAllByUserId(userId);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Транзакцію не знайдено"));
    }

    @Transactional
    public void updateTransaction(Long id, TransactionCreateDto dto) {
        if (dto.getDateTime().isAfter(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата транзакції не може бути у майбутньому");
        }

        Transaction transaction = getTransactionById(id);

        updateAccountBalance(transaction.getAccount(), transaction.getCategory(), transaction.getAmount(), true);

        transaction.setAmount(dto.getAmount());
        transaction.setDateTime(dto.getDateTime());
        transaction.setDescription(dto.getDescription());

        Account newAccount = accountRepository.findById(dto.getAccountId()).orElseThrow();
        Category newCategory = categoryRepository.findById(dto.getCategoryId()).orElseThrow();

        transaction.setAccount(newAccount);
        transaction.setCategory(newCategory);

        updateAccountBalance(newAccount, newCategory, dto.getAmount(), false);

        if (dto.getReceiptFile() != null && !dto.getReceiptFile().isEmpty()) {
            String newFileUrl = s3Service.uploadFile(dto.getReceiptFile());
            transaction.setReceiptFileUrl(newFileUrl);
        }

        transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransactionById(Long id) {
        Transaction transaction = getTransactionById(id);
        updateAccountBalance(transaction.getAccount(), transaction.getCategory(), transaction.getAmount(), true);
        transactionRepository.deleteById(id);
    }
}
