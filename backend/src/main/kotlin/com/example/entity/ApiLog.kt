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
    @Column(nullable = false)
    val apiName: String,
    
    @Column(nullable = false)
    val endpoint: String,
    
    @Column(nullable = false, length = 3000)
    val requestBody: String,
    
    // レスポンス情報
    @Column(length = 5000)
    val responseBody: String? = null,
    
    @Column(nullable = false)
    val successful: Boolean = false,
    
    @Column(length = 1000)
    val errorMessage: String? = null,
    
    // 実行時間情報
    @Column(nullable = false)
    val requestTimestamp: LocalDateTime = LocalDateTime.now(),
    
    @Column
    val responseTimestamp: LocalDateTime? = null,
    
    @Column
    val executionTimeMs: Long? = null,
    
    // 関連データ
    @Column
    val wordId: Long? = null
) 