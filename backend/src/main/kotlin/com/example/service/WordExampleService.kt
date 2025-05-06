package com.example.service

import com.example.dto.WordExampleDto
import com.example.entity.WordExample
import com.example.repository.WordExampleRepository
import com.example.repository.WordExampleRelationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordExampleService(
    private val wordExampleRepository: WordExampleRepository,
    private val wordExampleRelationRepository: WordExampleRelationRepository,
    private val wordExampleRelationService: WordExampleRelationService
) {

    @Transactional
    fun createExample(example: String, translation: String? = null, note: String? = null, source: String? = null): WordExampleDto {
        val wordExample = WordExample(
            example = example,
            translation = translation,
            note = note,
            source = source
        )
        
        val savedExample = wordExampleRepository.save(wordExample)
        return WordExampleDto.fromEntity(savedExample)
    }
    
    @Transactional
    fun addExampleToWord(wordId: Long, example: String, translation: String? = null, note: String? = null, source: String? = null): WordExampleDto {
        // 例文を作成
        val wordExample = WordExample(
            example = example,
            translation = translation,
            note = note,
            source = source
        )
        
        val savedExample = wordExampleRepository.save(wordExample)
        val exampleDto = WordExampleDto.fromEntity(savedExample)
        
        // 単語と例文を関連付け
        if (exampleDto.id != null) {
            wordExampleRelationService.addRelation(wordId, exampleDto.id)
        }
        
        return exampleDto
    }
    
    @Transactional(readOnly = true)
    fun getExampleById(id: Long): WordExampleDto? {
        val example = wordExampleRepository.findById(id).orElse(null) ?: return null
        return WordExampleDto.fromEntity(example)
    }
    
    @Transactional(readOnly = true)
    fun getExamplesByWordId(wordId: Long): List<WordExampleDto> {
        // 関連付けテーブルから単語に紐づく例文IDを取得
        val relations = wordExampleRelationRepository.findByWordId(wordId)
        val exampleIds = relations.map { it.exampleId }
        
        // 例文IDに対応する例文エンティティを取得
        return wordExampleRepository.findAllById(exampleIds)
            .map { WordExampleDto.fromEntity(it) }
    }
    
    // 互換性のために残す
    @Transactional(readOnly = true)
    fun getExamples(wordId: Long): List<WordExampleDto> {
        return getExamplesByWordId(wordId)
    }
    
    @Transactional
    fun updateExample(id: Long, example: String, translation: String? = null, note: String? = null, source: String? = null): WordExampleDto? {
        val existingExample = wordExampleRepository.findById(id).orElse(null) ?: return null
        
        val updatedExample = WordExample(
            id = existingExample.id,
            example = example,
            translation = translation,
            note = note,
            source = source
        )
        
        val savedExample = wordExampleRepository.save(updatedExample)
        return WordExampleDto.fromEntity(savedExample)
    }
    
    @Transactional
    fun deleteExample(id: Long): Boolean {
        if (!wordExampleRepository.existsById(id)) {
            return false
        }
        
        // 関連付けを先に削除
        wordExampleRelationService.deleteRelationsByExampleId(id)
        
        // 例文を削除
        wordExampleRepository.deleteById(id)
        return true
    }
    
    @Transactional
    fun removeExampleFromWord(wordId: Long, exampleId: Long): Boolean {
        return wordExampleRelationService.deleteRelation(wordId, exampleId)
    }
    
    @Transactional
    fun removeAllExamplesFromWord(wordId: Long) {
        wordExampleRelationService.deleteRelationsByWordId(wordId)
    }
    
    // 互換性のために残す
    @Transactional
    fun deleteAllExamples(wordId: Long) {
        removeAllExamplesFromWord(wordId)
    }
    
    @Transactional(readOnly = true)
    fun searchExamples(query: String): List<WordExampleDto> {
        return wordExampleRepository.findByExampleContaining(query)
            .map { WordExampleDto.fromEntity(it) }
    }
} 