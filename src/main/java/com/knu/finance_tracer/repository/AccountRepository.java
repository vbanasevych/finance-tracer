package com.knu.finance_tracer.repository;

import com.knu.finance_tracer.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByUserIdAndIsDeletedFalse(Long userId);

    Page<Account> findAllByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
}
