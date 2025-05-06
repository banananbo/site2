package com.example.dto

import com.example.entity.UserSession
import java.time.LocalDateTime

data class UserSessionDto(
    val id: String,
    val userId: Long,
    val auth0Id: String,
    val token: String?,
    val data: String?,
    val ipAddress: String?,
    val userAgent: String?,
    val createdAt: LocalDateTime,
    val lastAccessedAt: LocalDateTime,
    val expiresAt: LocalDateTime
) {
    companion object {
        fun fromEntity(entity: UserSession): UserSessionDto {
            return UserSessionDto(
                id = entity.id,
                userId = entity.user.id!!,
                auth0Id = entity.auth0Id,
                token = entity.token,
                data = entity.data,
                ipAddress = entity.ipAddress,
                userAgent = entity.userAgent,
                createdAt = entity.createdAt,
                lastAccessedAt = entity.lastAccessedAt,
                expiresAt = entity.expiresAt
            )
        }
    }
} 