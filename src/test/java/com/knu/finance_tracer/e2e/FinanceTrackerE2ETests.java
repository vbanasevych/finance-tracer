package com.knu.finance_tracer.e2e;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FinanceTrackerE2ETests {

    @BeforeAll
    public static void setup() {
        Configuration.baseUrl = "https://finance-tracker2026.duckdns.org";
        Configuration.holdBrowserOpen = true;
    }

    private void login() {
        open("/login");
        $(".card input[name='username']").setValue("test2-e2e@example.com");
        $(".card input[name='password']").setValue("TestPass123!");
        $(".card button[type='submit']").click();
        webdriver().shouldHave(urlContaining("/"));
    }

    @Test
    @Order(1)
    public void test2_createAccount() {
        login();

        open("/accounts/new");
        $(".card input[name='name']").setValue("Готівка");
        $(".card input[name='balance']").setValue("1000");
        $(".card button[type='submit']").click();
        $("body").shouldHave(text("Готівка"));

    }

    @Test
    @Order(2)
    public void test3_addTransactionAndUpdateBalance() {
        login();

        open("/transactions/new");

        $("select[name='accountId']").selectOption(0);
        $("select[name='categoryId']").selectOption(0);
        $("input[name='amount']").setValue("200");

        $(".card button[type='submit']").click();

        open("/accounts");
        $("body").shouldBe(visible);
    }

    @Test
    @Order(3)
    public void test4_analyticsPageRendersChart() {
        login();

        open("/analytics");
        $("h6").shouldHave(text("Витрати за категоріями"));
        $("#expensesChart").shouldBe(visible);
    }

    @Test
    @Order(4)
    public void test5_generateTelegramVerificationToken() {
        login();

        open("/profile");
        $x("//button[contains(text(), 'код') or contains(@formaction, 'telegram')]").click();
        $("body").shouldBe(visible);
    }

    @Test
    @Order(5)
    public void test6_standardUserCannotCreateCategory() {
        login();

        open("/categories/new");

        $("form[action*='/categories']").shouldNotBe(visible);

        $("body").shouldNotHave(text("Додати категорію"));
    }

    @Test
    @Order(6)
    public void test7_budgetCreationAndOverspendingFlow() {
        login();

        open("/budgets/new");
        $(".card input[name='limitAmount']").setValue("500");
        executeJavaScript("arguments[0].value = '2026-11-01';", $(".card input[name='startDate']"));
        executeJavaScript("arguments[0].value = '2026-11-30';", $(".card input[name='endDate']"));
        $("select[name='category.id']").selectOption(0);

        $(".card form button").click();

        $("body").shouldHave(text("500"));

        open("/transactions/new");
        executeJavaScript("arguments[0].value = '2026-11-01T12:00';", $(".card input[name='dateTime']"));
        $("select[name='accountId']").selectOption(0);
        $("select[name='categoryId']").selectOption(0);
        $("input[name='amount']").setValue("600");

        $(".card form button").click();


        open("/transactions");
        $("body").shouldHave(text("Перевищено бюджет"));
    }

    @Test
    @Order(7)
    public void test8_accountFullLifecycle_CreateAndThenDelete() {
        login();

        open("/accounts/new");
        $(".card input[name='name']").setValue("Тимчасовий рахунок");
        $(".card input[name='balance']").setValue("150");
        $(".card form button").click();

        $("body").shouldHave(text("Тимчасовий рахунок"));

        $x("//tr[td[contains(text(), 'Тимчасовий рахунок')]]//button[contains(text(), 'Видалити')]").click();

        $("body").shouldNotHave(text("Тимчасовий рахунок"));
    }
}