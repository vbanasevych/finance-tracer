package com.knu.finance_tracer.api;

import com.knu.finance_tracer.dto.TransactionCreateDto;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionApiControllerTest {

    @Mock private TransactionService transactionService;

    @InjectMocks
    private TransactionApiController controller;

    @Test
    void getTransactions_ShouldReturnPageAnd200() {
        Page<Transaction> page = new PageImpl<>(List.of(new Transaction()));
        when(transactionService.getTransactions(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<Transaction>> response = controller.getTransactions(0, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void getTransactionById_ShouldReturnTransactionAnd200() {
        Transaction transaction = new Transaction();
        when(transactionService.getTransactionById(1L)).thenReturn(transaction);

        ResponseEntity<Transaction> response = controller.getTransactionById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaction, response.getBody());
    }

    @Test
    void createTransaction_ShouldReturn201() {
        TransactionCreateDto dto = new TransactionCreateDto();

        ResponseEntity<Void> response = controller.createTransaction(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(transactionService).createTransaction(dto);
    }

    @Test
    void updateTransaction_ShouldReturn200() {
        TransactionCreateDto dto = new TransactionCreateDto();

        ResponseEntity<Void> response = controller.updateTransaction(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(transactionService).updateTransaction(1L, dto);
    }

    @Test
    void deleteTransaction_ShouldReturn204() {
        ResponseEntity<Void> response = controller.deleteTransaction(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transactionService).deleteTransactionById(1L);
    }
}
