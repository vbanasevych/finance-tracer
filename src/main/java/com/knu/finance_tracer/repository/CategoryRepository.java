package com.knu.finance_tracer.repository;

import com.knu.finance_tracer.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserIdAndIsDeletedFalse(Long userId);

    Page<Category> findAllByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
}
