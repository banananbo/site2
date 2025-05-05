package com.example.controller

import com.example.config.Auth0Config
import com.example.dto.AuthUrlResponse
import com.example.service.SessionService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val auth0Config: Auth0Config,
    private val sessionService: SessionService,
    private val env: Environment
) {

    @GetMapping("/login-url")
    fun getLoginUrl(): AuthUrlResponse {
        return AuthUrlResponse(auth0Config.getAuthorizeUrl())
    }
    
    /**
     * ログアウト処理
     * セッションを無効化し、ユーザーを未認証状態にする
     */
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Any> {
        val session = request.getSession(false)
        session?.invalidate()
        return ResponseEntity.ok().build()
    }

    @GetMapping("/logout-url")
    fun getLogoutUrl(): ResponseEntity<Map<String, String>> {
        val auth0Domain = env.getProperty("auth0.domain")
        val clientId = env.getProperty("auth0.clientId")
        val returnTo = env.getProperty("auth0.logoutRedirectUri") ?: "http://localhost"
        
        val logoutUrl = "https://$auth0Domain/v2/logout?client_id=$clientId&returnTo=$returnTo"
        
        return ResponseEntity.ok(mapOf("logoutUrl" to logoutUrl))
    }
} 