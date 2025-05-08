package com.example.controller

import com.example.dto.EnglishWordDto
import com.example.dto.EnglishWordRequest
import com.example.dto.EnglishWordResponse
import com.example.dto.WordExampleResponse
import com.example.service.EnglishWordService
import com.example.service.SessionService
import com.example.service.WordExampleService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/words")
class EnglishWordController(
    private val englishWordService: EnglishWordService,
    private val wordExampleService: WordExampleService,
    private val sessionService: SessionService
) {

    @PostMapping
    fun registerWord(
        @RequestBody request: EnglishWordRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<EnglishWordResponse> {
        // 認証チェック
        if (!sessionService.isLoggedIn(httpRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        
        val registeredWord = englishWordService.registerWord(request.word)
        
        val examples = if (registeredWord.id != null) {
            wordExampleService.getExamplesByWordId(registeredWord.id)
                .map { 
                    WordExampleResponse(
                        id = it.id!!,
                        example = it.example,
                        note = it.note,
                        source = it.source
                    ) 
                }
        } else {
            emptyList()
        }
        
        val response = EnglishWordResponse(
            id = registeredWord.id!!,
            word = registeredWord.word,
            meaning = registeredWord.meaning,
            examples = examples,
            translationStatus = registeredWord.translationStatus.name
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    @GetMapping
    fun getAllWords(): ResponseEntity<List<EnglishWordResponse>> {
        val words = englishWordService.getAllWords()
        
        val response = words.map {
            val examples = if (it.id != null) {
                wordExampleService.getExamplesByWordId(it.id)
                    .map { example -> 
                        WordExampleResponse(
                            id = example.id!!,
                            example = example.example,
                            note = example.note,
                            source = example.source
                        ) 
                    }
            } else {
                emptyList()
            }
            
            EnglishWordResponse(
                id = it.id!!,
                word = it.word,
                meaning = it.meaning,
                examples = examples,
                translationStatus = it.translationStatus.name
            )
        }
        
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/{id}")
    fun getWordById(@PathVariable id: Long): ResponseEntity<EnglishWordResponse> {
        val word = englishWordService.getWordById(id) ?: return ResponseEntity.notFound().build()
        
        val examples = wordExampleService.getExamplesByWordId(id)
            .map { 
                WordExampleResponse(
                    id = it.id!!,
                    example = it.example,
                    note = it.note,
                    source = it.source
                ) 
            }
        
        val response = EnglishWordResponse(
            id = word.id!!,
            word = word.word,
            meaning = word.meaning,
            examples = examples,
            translationStatus = word.translationStatus.name
        )
        
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/search")
    fun findWordByText(@RequestParam word: String): ResponseEntity<EnglishWordResponse> {
        val foundWord = englishWordService.getWordByText(word) ?: return ResponseEntity.notFound().build()
        
        val examples = foundWord.id?.let { wordExampleService.getExamplesByWordId(it) }
            ?.map { 
                WordExampleResponse(
                    id = it.id!!,
                    example = it.example,
                    note = it.note,
                    source = it.source
                ) 
            } ?: emptyList()
        
        val response = EnglishWordResponse(
            id = foundWord.id!!,
            word = foundWord.word,
            meaning = foundWord.meaning,
            examples = examples,
            translationStatus = foundWord.translationStatus.name
        )
        
        return ResponseEntity.ok(response)
    }
    
    @DeleteMapping("/{id}")
    fun deleteWord(@PathVariable id: Long): ResponseEntity<Void> {
        // 単語を削除する前に、関連する例文も削除
        wordExampleService.removeAllExamplesFromWord(id)
        
        val deleted = englishWordService.deleteWord(id)
        
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 