package com.example.dto

import com.example.entity.Grammar
import com.example.entity.Sentence
import com.example.common.DifficultyLevel
import java.time.LocalDateTime

/**
 * 文法パターンのDTO
 */
data class GrammarDto(
    val id: Long?,
    val pattern: String,
    val explanation: String?,
    val level: DifficultyLevel,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val sentenceCount: Int = 0
) {
    companion object {
        fun fromEntity(entity: Grammar): GrammarDto {
            return GrammarDto(
                id = entity.id,
                pattern = entity.pattern,
                explanation = entity.explanation,
                level = entity.level,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                sentenceCount = entity.sentences.size
            )
        }
    }
}

/**
 * 文法パターン登録リクエスト
 */
data class GrammarRequest(
    val pattern: String,
    val explanation: String,
    val level: String = "INTERMEDIATE"
)

/**
 * 文法パターン取得レスポンス
 */
data class GrammarResponse(
    val id: Long,
    val pattern: String,
    val explanation: String?,
    val level: String,
    val createdAt: String,
    val updatedAt: String
) 