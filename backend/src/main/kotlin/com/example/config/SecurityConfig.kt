package com.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/auth/login-url").permitAll()
                    .requestMatchers("/api/auth/token").permitAll()
                    .requestMatchers("/api/user/session").permitAll()
                    .requestMatchers("/api/user/logout").permitAll()
                    .requestMatchers("/api/hello").permitAll()
                    .anyRequest().authenticated()
            }
        return http.build()
    }
} 