package com.thinkitve.aidemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/providers/register",
                                "/api/v1/providers/register",
                                "/api/v1/providers",
                                "/api/v1/providers/*",
                                "/api/v1/provider/login",
                                "/api/v1/patient/register",
                                "/api/v1/patients/register",
                                "/api/v1/patients",
                                "/api/v1/patients/*",
                                "/api/v1/patient/login",
                                "/api/v1/provider/availability",
                                "/api/v1/provider/*/availability",
                                "/api/v1/availability/search",
                                "/h2-console/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // for H2 console

        return http.build();
    }
}
