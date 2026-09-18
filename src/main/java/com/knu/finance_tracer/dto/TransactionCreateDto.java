package com.knu.finance_tracer.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionCreateDto {
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private String description;
    private Long accountId;
    private Long categoryId;
    private MultipartFile receiptFile;
}
