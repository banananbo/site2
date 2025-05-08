package com.example.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "api_logs")
class ApiLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    // リクエスト情報
    @Column(name = "api_name", nullable = false)
    val apiName: String,
    
    @Column(nullable = false)
    val endpoint: String,
    
    @Column(name = "request_body", nullable = false, length = 3000)
    val requestBody: String,
    
    // レスポンス情報
    @Column(name = "response_body", length = 5000)
    val responseBody: String? = null,
    
    @Column(nullable = false)
    val successful: Boolean = false,
    
    @Column(name = "error_message", length = 1000)
    val errorMessage: String? = null,
    
    // 実行時間情報
    @Column(name = "request_timestamp", nullable = false)
    val requestTimestamp: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "response_timestamp")
    val responseTimestamp: LocalDateTime? = null,
    
    @Column(name = "execution_time_ms")
    val executionTimeMs: Long? = null,
    
    // 関連データ
    @Column(name = "word_id")
    val wordId: Long? = null
) 