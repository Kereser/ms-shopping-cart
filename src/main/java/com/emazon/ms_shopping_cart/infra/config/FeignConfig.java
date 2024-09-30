package com.emazon.ms_shopping_cart.infra.config;

import com.emazon.ms_shopping_cart.ConsUtils;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String token = getTokenFromAuth();
            if (token != null) {
                template.header(ConsUtils.AUTHORIZATION, token);
            }
        };
    }

    private String getTokenFromAuth() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getCredentials() != null) {
            return ConsUtils.BEARER + authentication.getCredentials();
        }

        return null;
    }
}
