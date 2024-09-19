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
        return template -> template.header(ConsUtils.AUTHORIZATION, getTokenFromAuth());
    }

    private String getTokenFromAuth() {
        return ConsUtils.BEARER + SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }
}
