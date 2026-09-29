package com.knu.finance_tracer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FinanceTracerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceTracerApplication.class, args);
	}

}
