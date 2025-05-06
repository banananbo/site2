package com.example.dto

import com.example.entity.WordExample

data class WordExampleDto(
    val id: Long? = null,
    val example: String,
    val translation: String? = null,
    val englishWordId: Long,
    val note: String? = null,
    val source: String? = null
) {
    companion object {
        fun fromEntity(entity: WordExample): WordExampleDto {
            return WordExampleDto(
                id = entity.id,
                example = entity.example,
                translation = entity.translation,
                englishWordId = entity.englishWordId,
                note = entity.note,
                source = entity.source
            )
        }
    }
    
    fun toEntity(): WordExample {
        return WordExample(
            id = this.id,
            example = this.example,
            translation = this.translation,
            englishWordId = this.englishWordId,
            note = this.note,
            source = this.source
        )
    }
}

data class WordExampleRequest(
    val example: String,
    val translation: String? = null,
    val note: String? = null,
    val source: String? = null
)

data class WordExampleResponse(
    val id: Long,
    val example: String,
    val translation: String? = null,
    val note: String? = null,
    val source: String? = null
) 