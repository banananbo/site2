package com.example.controller

import com.example.dto.TokenRequest
import com.example.dto.TokenResponse
import com.example.service.TokenService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class TokenController(private val tokenService: TokenService) {

    @PostMapping("/token")
    fun getToken(@RequestBody request: TokenRequest, httpRequest: HttpServletRequest): ResponseEntity<TokenResponse> {
        try {
            // Auth0からトークンを取得し、ユーザー登録とセッション作成を行う
            val auth0Response = tokenService.processCodeAndCreateSession(request.code, httpRequest)
            
            // フロントエンド用のレスポンスに変換
            val tokenResponse = tokenService.convertToTokenResponse(auth0Response)
            
            return ResponseEntity.ok(tokenResponse)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }
} 