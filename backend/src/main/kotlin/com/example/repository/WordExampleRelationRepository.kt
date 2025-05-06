package com.example.repository

import com.example.entity.WordExampleRelation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface WordExampleRelationRepository : JpaRepository<WordExampleRelation, Long> {
    fun findByWordId(wordId: Long): List<WordExampleRelation>
    fun findByExampleId(exampleId: Long): List<WordExampleRelation>
    fun findByWordIdAndExampleId(wordId: Long, exampleId: Long): WordExampleRelation?
    
    @Modifying
    @Query("DELETE FROM WordExampleRelation r WHERE r.wordId = :wordId")
    fun deleteByWordId(wordId: Long)
    
    @Modifying
    @Query("DELETE FROM WordExampleRelation r WHERE r.exampleId = :exampleId")
    fun deleteByExampleId(exampleId: Long)
    
    @Modifying
    @Query("DELETE FROM WordExampleRelation r WHERE r.wordId = :wordId AND r.exampleId = :exampleId")
    fun deleteByWordIdAndExampleId(wordId: Long, exampleId: Long)
} 