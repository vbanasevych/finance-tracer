package com.knu.finance_tracer.api;

import com.fasterxml.jackson.databind.JsonNode;
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

    public TelegramWebhookController(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> onUpdateReceived(@RequestBody JsonNode update) {
        telegramBotService.processWebhook(update);
        return ResponseEntity.ok().build();
    }
}
