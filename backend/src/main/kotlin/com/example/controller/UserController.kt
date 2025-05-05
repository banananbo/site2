package com.example.controller

import com.example.dto.UserDto
import com.example.service.UserService
import com.example.service.SessionService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
    private val sessionService: SessionService
) {

    /**
     * 現在のログイン状態を確認する
     */
    @GetMapping("/session")
    fun checkSession(request: HttpServletRequest): ResponseEntity<Map<String, Boolean>> {
        val isAuthenticated = sessionService.isLoggedIn(request)
        return ResponseEntity.ok(mapOf("authenticated" to isAuthenticated))
    }

    /**
     * 現在のユーザー情報を取得する
     */
    @GetMapping("/me")
    fun getCurrentUser(request: HttpServletRequest): ResponseEntity<Any> {
        return try {
            val user = userService.getCurrentUser(request)
            ResponseEntity.ok(user)
        } catch (e: Exception) {
            // エラーログを出力
            println("ユーザー情報取得エラー: ${e.message}")
            e.printStackTrace()
            
            // セッション情報をチェック
            val sessionExists = request.getSession(false) != null
            println("セッションが存在します: $sessionExists")
            
            // エラーレスポンスを返す
            ResponseEntity.status(401).body(mapOf(
                "error" to "unauthorized",
                "message" to "ユーザー情報の取得に失敗しました",
                "details" to e.message
            ))
        }
    }
} 