package com.example.service

import com.example.dto.UserSessionDto
import com.example.entity.User
import com.example.entity.UserSession
import com.example.repository.UserSessionRepository
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class DbSessionService(
    private val userSessionRepository: UserSessionRepository
) {
    companion object {
        private const val SESSION_COOKIE_NAME = "APP_SESSION"
        private const val DEFAULT_SESSION_TIMEOUT_MINUTES = 30L
    }
    
    @Value("\${app.session.timeout-minutes:30}")
    private val sessionTimeoutMinutes: Long = DEFAULT_SESSION_TIMEOUT_MINUTES
    
    @Value("\${app.session.cookie.secure:true}")
    private val secureCookie: Boolean = true
    
    @Value("\${app.session.cookie.http-only:true}")
    private val httpOnlyCookie: Boolean = true
    
    @Value("\${app.session.cookie.same-site:Lax}")
    private val sameSite: String = "Lax"

    /**
     * 新しいセッションを作成し、Cookieに保存する
     */
    @Transactional
    fun createSession(
        request: HttpServletRequest,
        response: HttpServletResponse,
        user: User,
        idToken: String? = null,
        data: String? = null
    ): UserSessionDto {
        // 既存のセッションがある場合は削除
        getSessionIdFromCookie(request)?.let { sessionId ->
            userSessionRepository.findById(sessionId).ifPresent { 
                userSessionRepository.delete(it)
            }
        }
        
        // 新しいセッションを作成
        val sessionId = UUID.randomUUID().toString()
        val expiresAt = LocalDateTime.now().plusMinutes(sessionTimeoutMinutes)
        
        val userSession = UserSession(
            id = sessionId,
            user = user,
            auth0Id = user.auth0Id,
            token = idToken,
            data = data,
            ipAddress = request.remoteAddr,
            userAgent = request.getHeader("User-Agent"),
            expiresAt = expiresAt
        )
        
        val savedSession = userSessionRepository.save(userSession)
        
        // Cookieにセッションを保存
        val cookie = Cookie(SESSION_COOKIE_NAME, sessionId).apply {
            path = "/"
            isHttpOnly = httpOnlyCookie
            secure = secureCookie
            maxAge = (sessionTimeoutMinutes * 60).toInt()
        }
        response.addCookie(cookie)
        response.addHeader("Set-Cookie", "${cookie.name}=${cookie.value}; Path=${cookie.path}; " +
                "Max-Age=${cookie.maxAge}; " +
                (if (cookie.secure) "Secure; " else "") +
                (if (cookie.isHttpOnly) "HttpOnly; " else "") +
                "SameSite=$sameSite")
        
        return UserSessionDto.fromEntity(savedSession)
    }
    
    /**
     * 既存のセッションを取得して有効期限を更新する
     */
    @Transactional
    fun getAndUpdateSession(request: HttpServletRequest): UserSessionDto? {
        val sessionId = getSessionIdFromCookie(request) ?: return null
        val now = LocalDateTime.now()
        
        val userSession = userSessionRepository.findByIdAndExpiresAtAfter(sessionId, now)
            .orElse(null) ?: return null
        
        // 最終アクセス時間を更新
        userSession.updateLastAccessed()
        // 有効期限を延長
        userSession.extend(sessionTimeoutMinutes)
        
        val updatedSession = userSessionRepository.save(userSession)
        return UserSessionDto.fromEntity(updatedSession)
    }
    
    /**
     * ユーザーセッションを無効化（ログアウト）
     */
    @Transactional
    fun invalidateSession(request: HttpServletRequest, response: HttpServletResponse) {
        val sessionId = getSessionIdFromCookie(request) ?: return
        
        // セッションを削除
        userSessionRepository.findById(sessionId).ifPresent {
            userSessionRepository.delete(it)
        }
        
        // Cookieを削除
        val cookie = Cookie(SESSION_COOKIE_NAME, "").apply {
            path = "/"
            maxAge = 0
        }
        response.addCookie(cookie)
    }
    
    /**
     * リクエストからセッションIDを取得
     */
    private fun getSessionIdFromCookie(request: HttpServletRequest): String? {
        val cookies = request.cookies ?: return null
        
        return cookies.find { it.name == SESSION_COOKIE_NAME }?.value
    }
    
    /**
     * 期限切れのセッションを定期的に削除
     */
    @Scheduled(fixedDelayString = "\${app.session.cleanup-interval-ms:3600000}")
    @Transactional
    fun cleanupExpiredSessions() {
        val now = LocalDateTime.now()
        val deleted = userSessionRepository.deleteExpiredSessions(now)
        if (deleted > 0) {
            println("Deleted $deleted expired sessions")
        }
    }
} 