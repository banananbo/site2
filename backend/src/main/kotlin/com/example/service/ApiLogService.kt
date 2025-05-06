package com.example.service

import com.example.entity.ApiLog
import com.example.repository.ApiLogRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ApiLogService(private val apiLogRepository: ApiLogRepository) {
    private val log = LoggerFactory.getLogger(ApiLogService::class.java)

    @Transactional
    fun createRequestLog(apiName: String, endpoint: String, requestBody: String, wordId: Long? = null): ApiLog {
        log.info("APIリクエストログを作成: $apiName - $endpoint")
        
        val apiLog = ApiLog(
            apiName = apiName,
            endpoint = endpoint,
            requestBody = requestBody,
            requestTimestamp = LocalDateTime.now(),
            wordId = wordId
        )
        
        return apiLogRepository.save(apiLog)
    }
    
    @Transactional
    fun updateWithResponse(apiLog: ApiLog, responseBody: String?, successful: Boolean, errorMessage: String? = null): ApiLog {
        val responseTime = LocalDateTime.now()
        val executionTimeMs = java.time.Duration.between(apiLog.requestTimestamp, responseTime).toMillis()
        
        log.info("APIレスポンスログを更新: ${apiLog.apiName} - ${apiLog.endpoint} - 実行時間: ${executionTimeMs}ms - 成功: $successful")
        
        val updatedLog = ApiLog(
            id = apiLog.id,
            apiName = apiLog.apiName,
            endpoint = apiLog.endpoint,
            requestBody = apiLog.requestBody,
            responseBody = responseBody,
            successful = successful,
            errorMessage = errorMessage,
            requestTimestamp = apiLog.requestTimestamp,
            responseTimestamp = responseTime,
            executionTimeMs = executionTimeMs,
            wordId = apiLog.wordId
        )
        
        return apiLogRepository.save(updatedLog)
    }
    
    @Transactional(readOnly = true)
    fun getLogsByApiName(apiName: String): List<ApiLog> {
        return apiLogRepository.findByApiName(apiName)
    }
    
    @Transactional(readOnly = true)
    fun getLogsByWordId(wordId: Long): List<ApiLog> {
        return apiLogRepository.findByWordId(wordId)
    }
    
    @Transactional(readOnly = true)
    fun getSuccessfulLogs(): List<ApiLog> {
        return apiLogRepository.findBySuccessful(true)
    }
    
    @Transactional(readOnly = true)
    fun getFailedLogs(): List<ApiLog> {
        return apiLogRepository.findBySuccessful(false)
    }
} 