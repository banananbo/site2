package com.example.repository

import com.example.entity.WordExample
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WordExampleRepository : JpaRepository<WordExample, Long> {
    fun findByExampleContaining(text: String): List<WordExample>
} 