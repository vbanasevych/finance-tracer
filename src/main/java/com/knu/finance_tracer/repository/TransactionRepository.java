package com.knu.finance_tracer.repository;


import com.knu.finance_tracer.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserId(Long userId);

    Page<Transaction> findAllByUserId(Long userId, Pageable pageable);
}
