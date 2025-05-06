package com.example.repository

import com.example.entity.Grammar
import com.example.common.DifficultyLevel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GrammarRepository : JpaRepository<Grammar, Long> {
    fun findByPattern(pattern: String): Grammar?
    fun findByPatternContaining(pattern: String): List<Grammar>
    fun findByLevel(level: DifficultyLevel): List<Grammar>
    fun existsByPattern(pattern: String): Boolean
} 