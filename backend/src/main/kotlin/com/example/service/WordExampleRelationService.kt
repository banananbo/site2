package com.example.service

import com.example.dto.WordExampleRelationDto
import com.example.entity.WordExampleRelation
import com.example.repository.WordExampleRelationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordExampleRelationService(private val wordExampleRelationRepository: WordExampleRelationRepository) {

    @Transactional
    fun addRelation(wordId: Long, exampleId: Long): WordExampleRelationDto {
        // 既存の関連がないか確認
        val existingRelation = wordExampleRelationRepository.findByWordIdAndExampleId(wordId, exampleId)
        
        if (existingRelation != null) {
            return WordExampleRelationDto.fromEntity(existingRelation)
        }
        
        val relation = WordExampleRelation(
            wordId = wordId,
            exampleId = exampleId
        )
        
        val savedRelation = wordExampleRelationRepository.save(relation)
        return WordExampleRelationDto.fromEntity(savedRelation)
    }
    
    @Transactional(readOnly = true)
    fun getRelationsByWordId(wordId: Long): List<WordExampleRelationDto> {
        return wordExampleRelationRepository.findByWordId(wordId)
            .map { WordExampleRelationDto.fromEntity(it) }
    }
    
    @Transactional(readOnly = true)
    fun getRelationsByExampleId(exampleId: Long): List<WordExampleRelationDto> {
        return wordExampleRelationRepository.findByExampleId(exampleId)
            .map { WordExampleRelationDto.fromEntity(it) }
    }
    
    @Transactional
    fun deleteRelation(wordId: Long, exampleId: Long): Boolean {
        wordExampleRelationRepository.deleteByWordIdAndExampleId(wordId, exampleId)
        return true
    }
    
    @Transactional
    fun deleteRelationsByWordId(wordId: Long) {
        wordExampleRelationRepository.deleteByWordId(wordId)
    }
    
    @Transactional
    fun deleteRelationsByExampleId(exampleId: Long) {
        wordExampleRelationRepository.deleteByExampleId(exampleId)
    }
} 