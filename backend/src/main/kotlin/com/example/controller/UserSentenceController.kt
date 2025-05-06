package com.example.controller

import com.example.dto.SentenceDto
import com.example.dto.UserSentenceRelationRequest
import com.example.dto.UserSentenceRelationResponse
import com.example.repository.UserRepository
import com.example.service.SentenceService
import com.example.service.UserService
import com.example.service.UserSentenceService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/sentences")
class UserSentenceController(
    private val userSentenceService: UserSentenceService,
    private val userService: UserService,
    private val sentenceService: SentenceService,
    private val userRepository: UserRepository
) {
    /**
     * 現在ログイン中のユーザーに関連付けられたセンテンスのリストを取得
     */
    @GetMapping
    fun getCurrentUserSentences(request: HttpServletRequest): ResponseEntity<List<SentenceDto>> {
        try {
            val currentUserDto = userService.getCurrentUser(request)
            // Auth0IDからデータベースのユーザーを取得
            val user = userRepository.findByAuth0Id(currentUserDto.id).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
            val sentences = userSentenceService.getUserSentences(user.id!!)
            val response = sentences.map { sentence -> 
                SentenceDto.fromEntity(sentence)
            }
            
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
    
    /**
     * 特定のユーザーIDに関連付けられたセンテンスのリストを取得（管理者用）
     */
    @GetMapping("/user/{userId}")
    fun getUserSentences(@PathVariable userId: Long): ResponseEntity<List<SentenceDto>> {
        val sentences = userSentenceService.getUserSentences(userId)
        val response = sentences.map { sentence -> 
            SentenceDto.fromEntity(sentence)
        }
        
        return ResponseEntity.ok(response)
    }
    
    /**
     * 現在ログイン中のユーザーとセンテンスを関連付ける
     */
    @PostMapping("/{sentenceId}")
    fun addSentenceToCurrentUser(
        @PathVariable sentenceId: Long,
        request: HttpServletRequest
    ): ResponseEntity<UserSentenceRelationResponse> {
        try {
            val currentUserDto = userService.getCurrentUser(request)
            // Auth0IDからデータベースのユーザーを取得
            val user = userRepository.findByAuth0Id(currentUserDto.id).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
            val relation = userSentenceService.addSentenceToUser(user.id!!, sentenceId)
            
            if (relation != null) {
                val response = UserSentenceRelationResponse(
                    id = relation.id!!,
                    userId = relation.user.id!!,
                    sentenceId = relation.sentence.id!!,
                    sentenceText = relation.sentence.text,
                    translation = relation.sentence.translation
                )
                return ResponseEntity.status(HttpStatus.CREATED).body(response)
            }
            
            return ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
    
    /**
     * 現在ログイン中のユーザーとセンテンスの関連を解除
     */
    @DeleteMapping("/{sentenceId}")
    fun removeSentenceFromCurrentUser(
        @PathVariable sentenceId: Long,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        try {
            val currentUserDto = userService.getCurrentUser(request)
            // Auth0IDからデータベースのユーザーを取得
            val user = userRepository.findByAuth0Id(currentUserDto.id).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
            val removed = userSentenceService.removeSentenceFromUser(user.id!!, sentenceId)
            
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
     * ユーザーが特定のセンテンスを持っているか確認
     */
    @GetMapping("/has/{sentenceId}")
    fun checkUserHasSentence(
        @PathVariable sentenceId: Long,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Boolean>> {
        try {
            val currentUserDto = userService.getCurrentUser(request)
            // Auth0IDからデータベースのユーザーを取得
            val user = userRepository.findByAuth0Id(currentUserDto.id).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
            val hasSentence = userSentenceService.hasUserSentence(user.id!!, sentenceId)
            
            return ResponseEntity.ok(mapOf("hasSentence" to hasSentence))
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
} 