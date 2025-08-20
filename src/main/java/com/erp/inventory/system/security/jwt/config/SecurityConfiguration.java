package com.erp.inventory.system.security.jwt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Whitelist public endpoints for authentication and Swagger UI
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Secure other endpoints based on roles
                        .requestMatchers("/api/v1/inventory/**").hasAnyAuthority("ROLE_INVENTORY_MANAGER", "ROLE_ADMIN")
                        .requestMatchers("/api/v1/supply-chain/**").hasAnyAuthority("ROLE_SUPPLY_CHAIN_COORDINATOR", "ROLE_ADMIN")
                        .requestMatchers("/api/v1/stores/**").hasAnyAuthority("ROLE_BUSINESS_OWNER", "ROLE_ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/v1/products/**").hasAnyAuthority("ROLE_INVENTORY_MANAGER", "ROLE_ADMIN")
                        .requestMatchers("/api/v1/categories/**").hasAnyAuthority("ROLE_INVENTORY_MANAGER", "ROLE_ADMIN")
                        .requestMatchers("/api/v1/suppliers/**").hasAnyAuthority("ROLE_SUPPLY_CHAIN_COORDINATOR", "ROLE_ADMIN")
                        .requestMatchers("/api/v1/warehouses/**").hasAnyAuthority("ROLE_INVENTORY_MANAGER", "ROLE_ADMIN")
                        .requestMatchers("/api/v1/inventory/**").hasAnyAuthority("ROLE_INVENTORY_MANAGER", "ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
