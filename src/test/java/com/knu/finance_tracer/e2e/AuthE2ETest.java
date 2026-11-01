package com.knu.finance_tracer.e2e;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class AuthE2ETest {

    @BeforeAll
    public static void setup() {
        Configuration.baseUrl = "https://finance-tracker2026.duckdns.org";
        Configuration.holdBrowserOpen = false;
    }

    @Test
    public void userCanRegisterAndLogin() {
        open("/register");

        $("input[name='name']").setValue("Вікторія Тест");
        $("input[name='email']").setValue("test2-e2e@example.com");
        $("input[name='passwordHash']").setValue("TestPass123!");

        $("button[type='submit']").click();

        webdriver().shouldHave(urlContaining("/login"));

        $("input[name='username']").setValue("test2-e2e@example.com");
        $("input[name='password']").setValue("TestPass123!");
        $("button[type='submit']").click();

        webdriver().shouldHave(urlContaining("/"));
        $("nav").shouldHave(text("test2-e2e@example.com"));
    }
}
