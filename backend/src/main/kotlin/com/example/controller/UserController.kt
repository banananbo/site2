package com.example.controller

import com.example.dto.UserSession
import com.example.service.SessionService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(private val sessionService: SessionService) {

    /**
     * 現在のログイン状態を確認する
     */
    @GetMapping("/session")
    fun checkSession(request: HttpServletRequest): ResponseEntity<UserSession> {
        val userSession = sessionService.getCurrentUser(request)
        return if (userSession != null) {
            ResponseEntity.ok(userSession)
        } else {
            ResponseEntity.noContent().build()
        }
    }
    
    /**
     * ログアウト処理
     */
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Void> {
        sessionService.invalidateSession(request)
        return ResponseEntity.ok().build()
    }
} 