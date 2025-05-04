package com.example.service

import com.example.dto.UserSession
import com.example.entity.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Service

@Service
class SessionService {
    companion object {
        private const val USER_SESSION_KEY = "user_session"
    }
    
    /**
     * ユーザーセッションを作成する
     */
    fun createUserSession(request: HttpServletRequest, user: User) {
        val session = request.session
        val userSession = UserSession(
            userId = user.id ?: throw IllegalStateException("ユーザーIDがありません"),
            auth0Id = user.auth0Id,
            email = user.email,
            name = user.name
        )
        session.setAttribute(USER_SESSION_KEY, userSession)
    }
    
    /**
     * 現在のセッションからユーザー情報を取得する
     */
    fun getCurrentUser(request: HttpServletRequest): UserSession? {
        val session = request.getSession(false) ?: return null
        return session.getAttribute(USER_SESSION_KEY) as? UserSession
    }
    
    /**
     * 現在のユーザーがログインしているかどうかを判定する
     */
    fun isLoggedIn(request: HttpServletRequest): Boolean {
        return getCurrentUser(request) != null
    }
    
    /**
     * ユーザーセッションを無効化する（ログアウト）
     */
    fun invalidateSession(request: HttpServletRequest) {
        val session = request.getSession(false)
        session?.invalidate()
    }
} 