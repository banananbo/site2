package com.example.repository

import com.example.entity.AccessToken
import com.example.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AccessTokenRepository : JpaRepository<AccessToken, Long> {
    fun findByUser(user: User): List<AccessToken>
    fun findFirstByUserOrderByCreatedAtDesc(user: User): Optional<AccessToken>
} 