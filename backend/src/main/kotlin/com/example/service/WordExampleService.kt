package com.example.service

import com.example.dto.WordExampleDto
import com.example.entity.WordExample
import com.example.repository.WordExampleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordExampleService(private val wordExampleRepository: WordExampleRepository) {

    @Transactional
    fun addExample(englishWordId: Long, example: String, translation: String? = null, note: String? = null, source: String? = null): WordExampleDto {
        val wordExample = WordExample(
            englishWordId = englishWordId,
            example = example,
            translation = translation,
            note = note,
            source = source
        )
        
        val savedExample = wordExampleRepository.save(wordExample)
        return WordExampleDto.fromEntity(savedExample)
    }
    
    @Transactional(readOnly = true)
    fun getExamples(englishWordId: Long): List<WordExampleDto> {
        return wordExampleRepository.findByEnglishWordId(englishWordId)
            .map { WordExampleDto.fromEntity(it) }
    }
    
    @Transactional
    fun updateExample(id: Long, example: String, translation: String? = null, note: String? = null, source: String? = null): WordExampleDto? {
        val existingExample = wordExampleRepository.findById(id).orElse(null) ?: return null
        
        val updatedExample = WordExample(
            id = existingExample.id,
            englishWordId = existingExample.englishWordId,
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
        
        wordExampleRepository.deleteById(id)
        return true
    }
    
    @Transactional
    fun deleteAllExamples(englishWordId: Long) {
        wordExampleRepository.deleteByEnglishWordId(englishWordId)
    }
} 