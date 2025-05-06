package com.example.dto

import com.example.entity.Sentence
import com.example.entity.Idiom
import com.example.common.DifficultyLevel
import com.example.common.TranslationStatus
import java.time.LocalDateTime

/**
 * センテンスのDTO
 */
data class SentenceDto(
    val id: Long? = null,
    val text: String,
    val translation: String? = null,
    val note: String? = null,
    val source: String? = null,
    val difficulty: DifficultyLevel = DifficultyLevel.INTERMEDIATE,
    val words: List<EnglishWordDto> = emptyList(),
    val idioms: List<IdiomDto> = emptyList(),
    val grammars: List<GrammarDto> = emptyList(),
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun fromEntity(entity: Sentence): SentenceDto {
            return SentenceDto(
                id = entity.id,
                text = entity.text,
                translation = entity.translation,
                note = entity.note,
                source = entity.source,
                difficulty = entity.difficulty,
                words = entity.words.map { EnglishWordDto.fromEntity(it) },
                idioms = entity.idioms.map { IdiomDto.fromEntity(it) },
                grammars = entity.grammars.map { GrammarDto.fromEntity(it) },
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}

/**
 * センテンス登録リクエスト
 */
data class SentenceRequest(
    val text: String,
    val translation: String? = null,
    val note: String? = null,
    val source: String? = null,
    val difficulty: String = "INTERMEDIATE"
)

/**
 * センテンス取得レスポンス
 */
data class SentenceResponse(
    val id: Long,
    val text: String,
    val translation: String? = null,
    val note: String? = null,
    val source: String? = null,
    val difficulty: String,
    val words: List<EnglishWordResponse> = emptyList(),
    val idioms: List<IdiomResponse> = emptyList(),
    val grammars: List<GrammarResponse> = emptyList(),
    val createdAt: String,
    val updatedAt: String
) 