package com.knu.finance_tracer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finance Tracker REST API")
                        .version("1.0.0")
                        .description("API для управління фінансами, рахунками, бюджетами та транзакціями (вимоги C5 та QA1)")
                        .contact(new Contact()
                                .name("Viktoriia")
                                .email("vbanasevych@knu.ua")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("finance-api")
                .pathsToMatch("/api/**")
                .build();
    }
}
