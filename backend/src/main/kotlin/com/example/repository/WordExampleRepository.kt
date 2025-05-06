package com.example.repository

import com.example.entity.WordExample
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface WordExampleRepository : JpaRepository<WordExample, Long> {
    fun findByEnglishWordId(englishWordId: Long): List<WordExample>
    
    @Modifying
    @Query("DELETE FROM WordExample w WHERE w.englishWordId = :englishWordId")
    fun deleteByEnglishWordId(englishWordId: Long)
} 