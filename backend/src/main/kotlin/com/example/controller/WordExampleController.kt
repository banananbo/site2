package com.example.controller

import com.example.dto.WordExampleRequest
import com.example.dto.WordExampleResponse
import com.example.service.WordExampleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/words/{wordId}/examples")
class WordExampleController(private val wordExampleService: WordExampleService) {

    @GetMapping
    fun getExamples(@PathVariable wordId: Long): ResponseEntity<List<WordExampleResponse>> {
        val examples = wordExampleService.getExamplesByWordId(wordId)
        
        val response = examples.map {
            WordExampleResponse(
                id = it.id!!,
                example = it.example,
                translation = it.translation,
                note = it.note,
                source = it.source
            )
        }
        
        return ResponseEntity.ok(response)
    }
    
    @PostMapping
    fun addExample(
        @PathVariable wordId: Long,
        @RequestBody request: WordExampleRequest
    ): ResponseEntity<WordExampleResponse> {
        val addedExample = wordExampleService.addExampleToWord(
            wordId = wordId,
            example = request.example,
            translation = request.translation,
            note = request.note,
            source = request.source
        )
        
        val response = WordExampleResponse(
            id = addedExample.id!!,
            example = addedExample.example,
            translation = addedExample.translation,
            note = addedExample.note,
            source = addedExample.source
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    @PutMapping("/{exampleId}")
    fun updateExample(
        @PathVariable wordId: Long,
        @PathVariable exampleId: Long,
        @RequestBody request: WordExampleRequest
    ): ResponseEntity<WordExampleResponse> {
        val updatedExample = wordExampleService.updateExample(
            id = exampleId,
            example = request.example,
            translation = request.translation,
            note = request.note,
            source = request.source
        ) ?: return ResponseEntity.notFound().build()
        
        val response = WordExampleResponse(
            id = updatedExample.id!!,
            example = updatedExample.example,
            translation = updatedExample.translation,
            note = updatedExample.note,
            source = updatedExample.source
        )
        
        return ResponseEntity.ok(response)
    }
    
    @DeleteMapping("/{exampleId}")
    fun deleteExample(
        @PathVariable wordId: Long,
        @PathVariable exampleId: Long,
        @RequestParam(required = false, defaultValue = "false") removeOnly: Boolean
    ): ResponseEntity<Void> {
        val deleted = if (removeOnly) {
            // 単語からのみ関連付けを削除
            wordExampleService.removeExampleFromWord(wordId, exampleId)
        } else {
            // 例文自体を削除
            wordExampleService.deleteExample(exampleId)
        }
        
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 