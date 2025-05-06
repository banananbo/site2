package com.example.repository

import com.example.entity.UserSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface UserSessionRepository : JpaRepository<UserSession, String> {
    
    fun findByIdAndExpiresAtAfter(id: String, now: LocalDateTime): Optional<UserSession>
    
    fun findByUserIdAndExpiresAtAfter(userId: Long, now: LocalDateTime): List<UserSession>
    
    fun findByAuth0IdAndExpiresAtAfter(auth0Id: String, now: LocalDateTime): List<UserSession>
    
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :now")
    fun deleteExpiredSessions(now: LocalDateTime): Int
    
    @Modifying
    @Query("UPDATE UserSession s SET s.lastAccessedAt = :now WHERE s.id = :id")
    fun updateLastAccessedTime(id: String, now: LocalDateTime): Int
} 