package com.knu.finance_tracer.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTest {

    @Test
    void showLandingPage_ShouldReturnIndex() {
        HomeController homeController = new HomeController();
        String viewName = homeController.showLandingPage();
        assertEquals("index", viewName);
    }
}
