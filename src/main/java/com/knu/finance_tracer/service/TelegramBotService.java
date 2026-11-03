package com.knu.finance_tracer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.knu.finance_tracer.entity.Account;
import com.knu.finance_tracer.entity.Category;
import com.knu.finance_tracer.entity.Role;
import com.knu.finance_tracer.entity.Transaction;
import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.repository.AccountRepository;
import com.knu.finance_tracer.repository.CategoryRepository;
import com.knu.finance_tracer.repository.TransactionRepository;
import com.knu.finance_tracer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TelegramBotService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final RestClient restClient;

    @Value("${telegram.bot.token}")
    private String botToken;

    public TelegramBotService(UserRepository userRepository,
                              TransactionRepository transactionRepository,
                              CategoryRepository categoryRepository,
                              AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
        this.restClient = RestClient.create("https://api.telegram.org");
    }

    @Transactional
    public void processWebhook(JsonNode update) {
        if (!update.has("message")) return;

        JsonNode message = update.get("message");
        if (!message.has("chat") || !message.has("text")) return;

        Long chatId = message.get("chat").get("id").asLong();
        String text = message.get("text").asText().trim();

        if (text.startsWith("/start")) {
            sendMessage(chatId, "Привіт! Щоб прив'язати акаунт, згенеруйте код у профілі нашого веб-додатку та надішліть його сюди у форматі: /link <ваш_код>");
            return;
        }

        if (text.startsWith("/link ")) {
            String token = text.substring(6).trim();

            User user = userRepository.findByTelegramVerificationToken(token).orElse(null);
            if (user == null) {
                sendMessage(chatId, "❌ Невірний або застарілий код прив'язки.");
                return;
            }

            user.setTelegramChatId(chatId);
            user.setTelegramVerificationToken(null);
            userRepository.save(user);

            sendMessage(chatId, "✅ Акаунт успішно прив'язано! Тепер ви можете надсилати витрати у форматі: 500 Кава");
            return;
        }

        User user = userRepository.findByTelegramChatId(chatId).orElse(null);
        if (user == null) {
            sendMessage(chatId, "Акаунт не підключено. Скористайтеся командою /start та прив'яжіть його через веб-сайт.");
            return;
        }

        try {
            String[] parts = text.split(" ", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException("Невірний формат");
            }

            BigDecimal amount = new BigDecimal(parts[0]);
            String categoryName = parts[1];

            List<Account> accounts = accountRepository.findAllByUserIdAndIsDeletedFalse(user.getId());
            if (accounts.isEmpty()) {
                sendMessage(chatId, "Помилка: у вас немає жодного активного рахунку у додатку.");
                return;
            }
            Account defaultAccount = accounts.get(0);

            List<Category> availableCategories = categoryRepository.findAllAvailableForUser(user.getId(), Role.ROLE_ADMIN);
            Category category = availableCategories.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName) && !c.getIsDeleted())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Категорію не знайдено"));

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDateTime(LocalDateTime.now());
            transaction.setAccount(defaultAccount);
            transaction.setCategory(category);
            transaction.setUser(user);
            transaction.setDescription("Створено через Telegram бота");

            transactionRepository.save(transaction);

            if (category.getIsExpense()) {
                defaultAccount.setBalance(defaultAccount.getBalance().subtract(amount));
            } else {
                defaultAccount.setBalance(defaultAccount.getBalance().add(amount));
            }
            accountRepository.save(defaultAccount);

            sendMessage(chatId, "✅ Успішно додано транзакцію: " + amount + " на категорію '" + category.getName() + "' (рахунок: " + defaultAccount.getName() + ")");

        } catch (Exception e) {
            sendMessage(chatId, "Помилка формату. Використовуйте: <Сума> <Категорія> (наприклад: 500 Продукти)");
        }
    }

    private void sendMessage(Long chatId, String text) {
        String url = "/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + text;
        try {
            restClient.get().uri(url).retrieve().toBodilessEntity();
        } catch (Exception e) { }
    }
}
