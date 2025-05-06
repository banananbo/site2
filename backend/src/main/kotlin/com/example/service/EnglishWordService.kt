package com.example.service

import com.example.common.TranslationStatus
import com.example.dto.EnglishWordDto
import com.example.entity.EnglishWord
import com.example.repository.EnglishWordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EnglishWordService(private val englishWordRepository: EnglishWordRepository) {

    @Transactional
    fun registerWord(word: String): EnglishWordDto {
        // 単語が既に存在するか確認
        if (englishWordRepository.existsByWord(word)) {
            // 既存の単語を返す
            val existingWord = englishWordRepository.findByWord(word)!!
            return EnglishWordDto.fromEntity(existingWord)
        }
        
        // 新しい単語を登録
        val newWord = EnglishWord(
            word = word,
            translationStatus = TranslationStatus.PENDING
        )
        
        val savedWord = englishWordRepository.save(newWord)
        return EnglishWordDto.fromEntity(savedWord)
    }
    
    @Transactional(readOnly = true)
    fun getAllWords(): List<EnglishWordDto> {
        return englishWordRepository.findAll().map { EnglishWordDto.fromEntity(it) }
    }
    
    @Transactional(readOnly = true)
    fun getWordById(id: Long): EnglishWordDto? {
        val word = englishWordRepository.findById(id).orElse(null) ?: return null
        return EnglishWordDto.fromEntity(word)
    }
    
    @Transactional(readOnly = true)
    fun getWordByText(word: String): EnglishWordDto? {
        val entity = englishWordRepository.findByWord(word) ?: return null
        return EnglishWordDto.fromEntity(entity)
    }
    
    @Transactional
    fun deleteWord(id: Long): Boolean {
        if (!englishWordRepository.existsById(id)) {
            return false
        }
        
        englishWordRepository.deleteById(id)
        return true
    }
} 