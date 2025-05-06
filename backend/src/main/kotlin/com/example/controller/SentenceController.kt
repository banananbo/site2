package com.example.controller

import com.example.dto.*
import com.example.service.SentenceService
import com.example.common.DifficultyLevel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/sentences")
class SentenceController(private val sentenceService: SentenceService) {

    private val dateFormatter = DateTimeFormatter.ISO_DATE_TIME

    @PostMapping
    fun registerSentence(@RequestBody request: SentenceRequest): ResponseEntity<SentenceResponse> {
        val registeredSentence = sentenceService.registerSentence(
            text = request.text,
            translation = request.translation,
            note = request.note,
            source = request.source,
            difficulty = request.difficulty
        )
        
        val response = createSentenceResponse(registeredSentence)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    @GetMapping
    fun getAllSentences(): ResponseEntity<List<SentenceResponse>> {
        val sentences = sentenceService.getAllSentences()
        val response = sentences.map { createSentenceResponse(it) }
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/{id}")
    fun getSentenceById(@PathVariable id: Long): ResponseEntity<SentenceResponse> {
        val sentence = sentenceService.getSentenceById(id) ?: return ResponseEntity.notFound().build()
        val response = createSentenceResponse(sentence)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/difficulty/{level}")
    fun getSentencesByDifficulty(@PathVariable level: String): ResponseEntity<List<SentenceResponse>> {
        val sentences = sentenceService.getSentencesByDifficulty(level)
        val response = sentences.map { createSentenceResponse(it) }
        return ResponseEntity.ok(response)
    }
    
    @PutMapping("/{id}")
    fun updateSentence(
        @PathVariable id: Long,
        @RequestBody request: SentenceRequest
    ): ResponseEntity<SentenceResponse> {
        val updatedSentence = sentenceService.updateSentence(
            id = id,
            text = request.text,
            translation = request.translation,
            note = request.note,
            source = request.source,
            difficulty = request.difficulty
        ) ?: return ResponseEntity.notFound().build()
        
        val response = createSentenceResponse(updatedSentence)
        return ResponseEntity.ok(response)
    }
    
    @DeleteMapping("/{id}")
    fun deleteSentence(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = sentenceService.deleteSentence(id)
        
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    /**
     * DTOからレスポンスオブジェクトを作成するヘルパーメソッド
     */
    private fun createSentenceResponse(dto: SentenceDto): SentenceResponse {
        return SentenceResponse(
            id = dto.id!!,
            text = dto.text,
            translation = dto.translation,
            note = dto.note,
            source = dto.source,
            difficulty = dto.difficulty.name,
            words = dto.words.map { word ->
                EnglishWordResponse(
                    id = word.id!!,
                    word = word.word,
                    meaning = word.meaning,
                    translationStatus = word.translationStatus.name
                )
            },
            idioms = dto.idioms.map { idiom ->
                IdiomResponse(
                    id = idiom.id!!,
                    phrase = idiom.phrase,
                    meaning = idiom.meaning,
                    explanation = idiom.explanation,
                    createdAt = idiom.createdAt?.format(dateFormatter) ?: "",
                    updatedAt = idiom.updatedAt?.format(dateFormatter) ?: ""
                )
            },
            grammars = dto.grammars.map { grammar ->
                GrammarResponse(
                    id = grammar.id!!,
                    pattern = grammar.pattern,
                    explanation = grammar.explanation,
                    level = grammar.level.name,
                    createdAt = grammar.createdAt?.format(dateFormatter) ?: "",
                    updatedAt = grammar.updatedAt?.format(dateFormatter) ?: ""
                )
            },
            createdAt = dto.createdAt?.format(dateFormatter) ?: "",
            updatedAt = dto.updatedAt?.format(dateFormatter) ?: ""
        )
    }
} 