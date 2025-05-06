package com.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { authorize ->
                authorize
                    // 認証関連エンドポイント
                    .requestMatchers("/api/auth/login-url").permitAll()
                    .requestMatchers("/api/auth/token").permitAll()
                    .requestMatchers("/api/auth/logout").permitAll()
                    .requestMatchers("/api/auth/logout-url").permitAll()
                    
                    // ユーザー情報関連エンドポイント
                    .requestMatchers("/api/user/session").permitAll()
                    .requestMatchers("/api/user/me").permitAll() // 現在は公開アクセス許可
                    
                    // ユーザー単語関連エンドポイント
                    .requestMatchers("/api/user/words/**").permitAll() // 一時的に全許可
                    
                    // その他のパブリックAPI
                    .requestMatchers("/api/hello").permitAll()
                    
                    // 英単語API
                    .requestMatchers("/api/words/**").permitAll()
                    
                    // センテンスAPI
                    .requestMatchers("/api/sentences/**").permitAll()
                    
                    // APIログAPI
                    .requestMatchers("/api/logs/**").permitAll()
                    
                    // その他のリクエストは認証必須
                    .anyRequest().authenticated()
            }
        return http.build()
    }
    
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://lvh.me", "http://localhost:5173", "http://localhost:3000")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.maxAge = 3600
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
} 