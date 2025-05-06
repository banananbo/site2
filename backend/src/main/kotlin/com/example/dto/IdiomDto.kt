package com.example.dto

import com.example.entity.Idiom
import java.time.LocalDateTime

/**
 * イディオムのDTO
 */
data class IdiomDto(
    val id: Long? = null,
    val phrase: String,
    val meaning: String? = null,
    val explanation: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun fromEntity(entity: Idiom): IdiomDto {
            return IdiomDto(
                id = entity.id,
                phrase = entity.phrase,
                meaning = entity.meaning,
                explanation = entity.explanation,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}

/**
 * イディオム登録リクエスト
 */
data class IdiomRequest(
    val phrase: String,
    val meaning: String? = null,
    val explanation: String? = null
)

/**
 * イディオム取得レスポンス
 */
data class IdiomResponse(
    val id: Long,
    val phrase: String,
    val meaning: String? = null,
    val explanation: String? = null,
    val createdAt: String,
    val updatedAt: String
) 