package com.knu.finance_tracer.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgumentException_ShouldReturn400() {
        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgumentException(
                new IllegalArgumentException("Invalid data"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Invalid data", response.getBody().get("message"));
    }

    @Test
    void handleGeneralException_ShouldReturn500() {
        ResponseEntity<Map<String, String>> response = handler.handleGeneralException(
                new Exception("Database down"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("Database down", response.getBody().get("message"));
    }
}
