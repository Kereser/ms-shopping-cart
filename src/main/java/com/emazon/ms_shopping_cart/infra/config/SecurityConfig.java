package com.emazon.ms_shopping_cart.infra.config;

import com.emazon.ms_shopping_cart.ConsUtils;
import com.emazon.ms_shopping_cart.infra.security.entrypoint.CustomBasicAuthenticationEntryPoint;
import com.emazon.ms_shopping_cart.infra.security.entrypoint.CustomJWTEntryPoint;
import com.emazon.ms_shopping_cart.infra.security.filter.JwtValidatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomJWTEntryPoint jwtEntryPoint) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(authenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(HttpMethod.PUT, ConsUtils.builderPath().build()).hasRole(ConsUtils.CLIENT);
                auth.requestMatchers(HttpMethod.DELETE, ConsUtils.builderPath().withCartId().withArticles().withArticleId().build()).hasRole(ConsUtils.CLIENT);
                auth.requestMatchers(HttpMethod.GET, ConsUtils.builderPath().withCartId().withArticles().build()).permitAll();

                auth.anyRequest().denyAll();
            });

        http.anonymous(AbstractHttpConfigurer::disable);
        http.addFilterBefore(new JwtValidatorFilter(jwtEntryPoint), BasicAuthenticationFilter.class);

        return http.build();
    }
}
