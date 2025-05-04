package com.example.controller

import com.example.config.Auth0Config
import com.example.dto.AuthUrlResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val auth0Config: Auth0Config) {

    @GetMapping("/login-url")
    fun getLoginUrl(): AuthUrlResponse {
        return AuthUrlResponse(auth0Config.getAuthorizeUrl())
    }
} 