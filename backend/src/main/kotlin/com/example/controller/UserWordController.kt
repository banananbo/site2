package com.example.controller

import com.example.dto.EnglishWordResponse
import com.example.dto.UserWordRelationRequest
import com.example.dto.UserWordRelationResponse
import com.example.repository.UserRepository
import com.example.service.EnglishWordService
import com.example.service.UserService
import com.example.service.UserWordService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/words")
class UserWordController(
    private val userWordService: UserWordService,
    private val userService: UserService,
    private val englishWordService: EnglishWordService,
    private val userRepository: UserRepository
) {
    /**
     * 現在ログイン中のユーザーに関連付けられた単語のリストを取得
     */
    @GetMapping
    fun getCurrentUserWords(request: HttpServletRequest): ResponseEntity<List<EnglishWordResponse>> {
        try {
            val currentUserDto = userService.getCurrentUser(request)
            // Auth0IDからデータベースのユーザーを取得
            val user = userRepository.findByAuth0Id(currentUserDto.id).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
            val words = userWordService.getUserWords(user.id!!)
            val response = words.map { word ->
                EnglishWordResponse(
                    id = word.id!!,
                    word = word.word,
                    meaning = word.meaning,
                    examples = emptyList(),
                    translationStatus = word.translationStatus.name
                )
            }
            
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
    
    /**
     * 特定のユーザーIDに関連付けられた単語のリストを取得（管理者用）
     */
    @GetMapping("/user/{userId}")
    fun getUserWords(@PathVariable userId: Long): ResponseEntity<List<EnglishWordResponse>> {
        val words = userWordService.getUserWords(userId)
        val response = words.map { word ->
            EnglishWordResponse(
                id = word.id!!,
                word = word.word,
                meaning = word.meaning,
                examples = emptyList(),
                translationStatus = word.translationStatus.name
            )
        }
        
        return ResponseEntity.ok(response)
    }
    
    /**
     * 現在ログイン中のユーザーと単語を関連付ける
     */
    @PostMapping("/{wordId}")
    fun addWordToCurrentUser(
        @PathVariable wordId: Long,
        request: HttpServletRequest
    ): ResponseEntity<UserWordRelationResponse> {
        try {
            val currentUserDto = userService.getCurrentUser(request)
            // Auth0IDからデータベースのユーザーを取得
            val user = userRepository.findByAuth0Id(currentUserDto.id).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
            val relation = userWordService.addWordToUser(user.id!!, wordId)
            
            if (relation != null) {
                val response = UserWordRelationResponse(
                    id = relation.id!!,
                    userId = relation.user.id!!,
                    wordId = relation.word.id!!,
                    wordText = relation.word.word
                )
                return ResponseEntity.status(HttpStatus.CREATED).body(response)
            }
            
            return ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
    
    /**
     * 現在ログイン中のユーザーと単語の関連を解除
     */
    @DeleteMapping("/{wordId}")
    fun removeWordFromCurrentUser(
        @PathVariable wordId: Long,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        try {
            val currentUserDto = userService.getCurrentUser(request)
            // Auth0IDからデータベースのユーザーを取得
            val user = userRepository.findByAuth0Id(currentUserDto.id).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
            val removed = userWordService.removeWordFromUser(user.id!!, wordId)
            
            return if (removed) {
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
    
    /**
     * ユーザーが特定の単語を持っているか確認
     */
    @GetMapping("/has/{wordId}")
    fun checkUserHasWord(
        @PathVariable wordId: Long,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Boolean>> {
        try {
            val currentUserDto = userService.getCurrentUser(request)
            // Auth0IDからデータベースのユーザーを取得
            val user = userRepository.findByAuth0Id(currentUserDto.id).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
            val hasWord = userWordService.hasUserWord(user.id!!, wordId)
            
            return ResponseEntity.ok(mapOf("hasWord" to hasWord))
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
} 