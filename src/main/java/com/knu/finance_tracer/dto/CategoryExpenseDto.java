package com.knu.finance_tracer.dto;

import java.math.BigDecimal;

public record CategoryExpenseDto(
        String categoryName,
        BigDecimal totalAmount
) {}
