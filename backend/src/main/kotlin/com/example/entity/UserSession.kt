package com.example.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_sessions")
class UserSession(
    @Id
    val id: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    @Column(name = "auth0_id", nullable = false)
    val auth0Id: String,
    
    @Column(name = "token", length = 1024)
    var token: String? = null,
    
    @Column(name = "data", columnDefinition = "TEXT")
    var data: String? = null,
    
    @Column(name = "ip_address", length = 45)
    val ipAddress: String? = null,
    
    @Column(name = "user_agent")
    val userAgent: String? = null,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "last_accessed_at", nullable = false)
    var lastAccessedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime
) {
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }
    
    fun updateLastAccessed() {
        this.lastAccessedAt = LocalDateTime.now()
    }
    
    fun extend(minutes: Long) {
        this.expiresAt = LocalDateTime.now().plusMinutes(minutes)
    }
} 