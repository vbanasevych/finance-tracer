package com.knu.finance_tracer.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.access.expression.DefaultHttpSecurityExpressionHandler;

@TestConfiguration
public class ControllerTestConfig {

    @Bean
    SecurityExpressionHandler<FilterInvocation> securityExpressionHandler() {
        DefaultHttpSecurityExpressionHandler delegate = new DefaultHttpSecurityExpressionHandler();
        return new SecurityExpressionHandler<>() {
            @Override
            public ExpressionParser getExpressionParser() {
                return delegate.getExpressionParser();
            }

            @Override
            public EvaluationContext createEvaluationContext(Authentication authentication,
                                                              FilterInvocation invocation) {
                return delegate.createEvaluationContext(authentication,
                        new RequestAuthorizationContext(invocation.getHttpRequest()));
            }
        };
    }
}
