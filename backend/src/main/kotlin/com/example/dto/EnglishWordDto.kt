package com.example.dto

import com.example.entity.EnglishWord
import com.example.entity.WordExample
import com.example.common.TranslationStatus
import java.time.LocalDateTime

/**
 * 英単語のDTOクラス
 */
data class EnglishWordDto(
    val id: Long?,
    val word: String,
    val meaning: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val translationStatus: TranslationStatus,
    val examples: List<WordExampleDto> = emptyList()
) {
    companion object {
        fun fromEntity(entity: EnglishWord): EnglishWordDto {
            return EnglishWordDto(
                id = entity.id,
                word = entity.word,
                meaning = entity.meaning,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                translationStatus = entity.translationStatus,
                examples = entity.examples.map { WordExampleDto.fromEntity(it) }
            )
        }
    }
    
    fun toEntity(): EnglishWord {
        return EnglishWord(
            id = this.id,
            word = this.word,
            meaning = this.meaning,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            translationStatus = this.translationStatus
        )
    }
}

data class EnglishWordRequest(
    val word: String
)

/**
 * 英単語のレスポンスクラス
 */
data class EnglishWordResponse(
    val id: Long,
    val word: String,
    val meaning: String? = null,
    val examples: List<WordExampleResponse> = emptyList(),
    val translationStatus: String
) 