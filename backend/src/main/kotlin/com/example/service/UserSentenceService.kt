package com.example.service

import com.example.entity.Sentence
import com.example.entity.User
import com.example.entity.UserSentenceRelation
import com.example.repository.SentenceRepository
import com.example.repository.UserRepository
import com.example.repository.UserSentenceRelationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserSentenceService(
    private val userRepository: UserRepository,
    private val sentenceRepository: SentenceRepository,
    private val userSentenceRelationRepository: UserSentenceRelationRepository
) {
    /**
     * ユーザーIDに関連付けられたセンテンスのリストを取得
     */
    fun getUserSentences(userId: Long): List<Sentence> {
        return userSentenceRelationRepository.findByUserId(userId).map { it.sentence }
    }
    
    /**
     * ユーザーとセンテンスを関連付ける
     */
    @Transactional
    fun addSentenceToUser(userId: Long, sentenceId: Long): UserSentenceRelation? {
        val userOptional: Optional<User> = userRepository.findById(userId)
        val sentenceOptional: Optional<Sentence> = sentenceRepository.findById(sentenceId)
        
        if (userOptional.isPresent && sentenceOptional.isPresent) {
            val user = userOptional.get()
            val sentence = sentenceOptional.get()
            
            // 既に関連が存在するか確認
            val existingRelation = userSentenceRelationRepository.findByUserIdAndSentenceId(userId, sentenceId)
            if (existingRelation != null) {
                return existingRelation
            }
            
            // 新しい関連を作成
            val relation = UserSentenceRelation(
                user = user,
                sentence = sentence
            )
            return userSentenceRelationRepository.save(relation)
        }
        return null
    }
    
    /**
     * ユーザーとセンテンスの関連を解除
     */
    @Transactional
    fun removeSentenceFromUser(userId: Long, sentenceId: Long): Boolean {
        val relation = userSentenceRelationRepository.findByUserIdAndSentenceId(userId, sentenceId)
        if (relation != null) {
            userSentenceRelationRepository.delete(relation)
            return true
        }
        return false
    }
    
    /**
     * ユーザーが特定のセンテンスを持っているか確認
     */
    fun hasUserSentence(userId: Long, sentenceId: Long): Boolean {
        return userSentenceRelationRepository.findByUserIdAndSentenceId(userId, sentenceId) != null
    }
} 