package com.knu.finance_tracer.service;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.repository.AccountRepository;
import com.knu.finance_tracer.repository.CategoryRepository;
import com.knu.finance_tracer.repository.TransactionRepository;
import com.knu.finance_tracer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void createTransaction(TransactionCreateDto dto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setDateTime(dto.getDateTime());
        transaction.setDescription(dto.getDescription());

        String fileUrl = s3Service.uploadFile(dto.getReceiptFile());
        transaction.setReceiptFileUrl(fileUrl);

        // для тесту взято існуючі записи
        transaction.setAccount(accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Рахунок не знайдено")));
        transaction.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Категорію не знайдено")));

        // тимчасовий хардкод користувача до впровадження Spring Security
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
        Transaction transaction = getTransactionById(id);
        transaction.setAmount(dto.getAmount());
        transaction.setDateTime(dto.getDateTime());
        transaction.setDescription(dto.getDescription());

        transaction.setAccount(accountRepository.findById(dto.getAccountId()).orElseThrow());
        transaction.setCategory(categoryRepository.findById(dto.getCategoryId()).orElseThrow());

        if (dto.getReceiptFile() != null && !dto.getReceiptFile().isEmpty()) {
            String newFileUrl = s3Service.uploadFile(dto.getReceiptFile());
            transaction.setReceiptFileUrl(newFileUrl);
        }

        transactionRepository.save(transaction);
    }

    public void deleteTransactionById(Long id) {
        transactionRepository.deleteById(id);
    }
}
