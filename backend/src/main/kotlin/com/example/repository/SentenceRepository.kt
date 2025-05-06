package com.example.repository

import com.example.entity.Sentence
import com.example.common.DifficultyLevel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SentenceRepository : JpaRepository<Sentence, Long> {
    fun findByText(text: String): Sentence?
    fun findByTextContaining(text: String): List<Sentence>
    fun findByDifficulty(level: DifficultyLevel): List<Sentence>
} 