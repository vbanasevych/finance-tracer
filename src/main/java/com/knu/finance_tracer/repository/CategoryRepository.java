package com.knu.finance_tracer.repository;

import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUserIdAndIsDeletedFalse(Long userId);
    Page<Category> findAllByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE (c.user.id = :userId OR c.user.role = :adminRole) AND c.isDeleted = false")
    List<Category> findAllAvailableForUser(@Param("userId") Long userId, @Param("adminRole") Role adminRole);

    @Query("SELECT c FROM Category c WHERE (c.user.id = :userId OR c.user.role = :adminRole) AND c.isDeleted = false")
    Page<Category> findAllAvailableForUser(@Param("userId") Long userId, @Param("adminRole") Role adminRole, Pageable pageable);

}
