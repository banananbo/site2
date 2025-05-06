package com.example.dto

import com.example.entity.EnglishWord
import com.example.entity.TranslationStatus
import com.example.entity.WordExample
import java.time.LocalDateTime

data class EnglishWordDto(
    val id: Long? = null,
    val word: String,
    val meaning: String? = null,
    val examples: List<WordExampleDto> = emptyList(),
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val translationStatus: TranslationStatus = TranslationStatus.PENDING
) {
    companion object {
        fun fromEntity(entity: EnglishWord): EnglishWordDto {
            return EnglishWordDto(
                id = entity.id,
                word = entity.word,
                meaning = entity.meaning,
                examples = entity.examples.map { WordExampleDto.fromEntity(it) },
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                translationStatus = entity.translationStatus
            )
        }
    }
    
    fun toEntity(): EnglishWord {
        return EnglishWord(
            id = this.id,
            word = this.word,
            meaning = this.meaning,
            createdAt = this.createdAt ?: LocalDateTime.now(),
            updatedAt = this.updatedAt ?: LocalDateTime.now(),
            translationStatus = this.translationStatus
        )
    }
}

data class EnglishWordRequest(
    val word: String
)

data class EnglishWordResponse(
    val id: Long,
    val word: String,
    val meaning: String? = null,
    val examples: List<WordExampleResponse> = emptyList(),
    val translationStatus: String
) 