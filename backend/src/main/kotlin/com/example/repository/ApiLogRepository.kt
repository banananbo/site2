package com.example.repository

import com.example.entity.ApiLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ApiLogRepository : JpaRepository<ApiLog, Long> {
    fun findByApiName(apiName: String): List<ApiLog>
    fun findByWordId(wordId: Long): List<ApiLog>
    fun findByRequestTimestampBetween(startTime: LocalDateTime, endTime: LocalDateTime): List<ApiLog>
    fun findBySuccessful(successful: Boolean): List<ApiLog>
} 