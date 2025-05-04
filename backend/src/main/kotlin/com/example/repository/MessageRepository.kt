package com.example.repository

import com.example.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {
    fun findFirstByOrderByIdAsc(): Message?
} 