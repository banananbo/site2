package com.example.controller

import com.example.dto.WordExampleRelationRequest
import com.example.dto.WordExampleRelationResponse
import com.example.service.WordExampleRelationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/word-example-relations")
class WordExampleRelationController(private val wordExampleRelationService: WordExampleRelationService) {

    @PostMapping
    fun addRelation(@RequestBody request: WordExampleRelationRequest): ResponseEntity<WordExampleRelationResponse> {
        val relation = wordExampleRelationService.addRelation(
            wordId = request.wordId,
            exampleId = request.exampleId
        )
        
        val response = WordExampleRelationResponse(
            id = relation.id!!,
            wordId = relation.wordId,
            exampleId = relation.exampleId
        )
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    @GetMapping("/word/{wordId}")
    fun getRelationsByWordId(@PathVariable wordId: Long): ResponseEntity<List<WordExampleRelationResponse>> {
        val relations = wordExampleRelationService.getRelationsByWordId(wordId)
        
        val response = relations.map {
            WordExampleRelationResponse(
                id = it.id!!,
                wordId = it.wordId,
                exampleId = it.exampleId
            )
        }
        
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/example/{exampleId}")
    fun getRelationsByExampleId(@PathVariable exampleId: Long): ResponseEntity<List<WordExampleRelationResponse>> {
        val relations = wordExampleRelationService.getRelationsByExampleId(exampleId)
        
        val response = relations.map {
            WordExampleRelationResponse(
                id = it.id!!,
                wordId = it.wordId,
                exampleId = it.exampleId
            )
        }
        
        return ResponseEntity.ok(response)
    }
    
    @DeleteMapping
    fun deleteRelation(
        @RequestParam wordId: Long,
        @RequestParam exampleId: Long
    ): ResponseEntity<Void> {
        val deleted = wordExampleRelationService.deleteRelation(wordId, exampleId)
        
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 