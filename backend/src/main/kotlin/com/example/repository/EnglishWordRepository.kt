package com.example.repository

import com.example.entity.EnglishWord
import com.example.common.TranslationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EnglishWordRepository : JpaRepository<EnglishWord, Long> {
    fun findByWord(word: String): EnglishWord?
    fun existsByWord(word: String): Boolean
    
    // 翻訳ステータスによる検索
    fun findByTranslationStatus(status: TranslationStatus): List<EnglishWord>
    
    // 翻訳ステータスによるカウント
    fun countByTranslationStatus(status: TranslationStatus): Long
} 