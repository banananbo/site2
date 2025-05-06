package com.example.dto

import com.example.entity.WordExampleRelation

data class WordExampleRelationDto(
    val id: Long? = null,
    val wordId: Long,
    val exampleId: Long
) {
    companion object {
        fun fromEntity(entity: WordExampleRelation): WordExampleRelationDto {
            return WordExampleRelationDto(
                id = entity.id,
                wordId = entity.wordId,
                exampleId = entity.exampleId
            )
        }
    }
    
    fun toEntity(): WordExampleRelation {
        return WordExampleRelation(
            id = this.id,
            wordId = this.wordId,
            exampleId = this.exampleId
        )
    }
}

data class WordExampleRelationRequest(
    val wordId: Long,
    val exampleId: Long
)

data class WordExampleRelationResponse(
    val id: Long,
    val wordId: Long,
    val exampleId: Long
) 