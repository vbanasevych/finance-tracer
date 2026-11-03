package com.knu.finance_tracer.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knu.finance_tracer.service.TelegramBotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/telegram")
public class TelegramWebhookController {

    private final TelegramBotService telegramBotService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelegramWebhookController(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody String rawJson) {
        System.out.println(">>> OТРИМАНО ВЕБХУК ВІД TELEGRAM: " + rawJson);
        try {
            JsonNode update = objectMapper.readTree(rawJson);
            telegramBotService.processWebhook(update);
        } catch (Exception e) {
            System.err.println("Помилка обробки вебхука: " + e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }
}
