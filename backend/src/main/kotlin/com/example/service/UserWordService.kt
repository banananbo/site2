package com.example.service

import com.example.entity.EnglishWord
import com.example.entity.User
import com.example.entity.UserWordRelation
import com.example.repository.EnglishWordRepository
import com.example.repository.UserRepository
import com.example.repository.UserWordRelationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserWordService(
    private val userRepository: UserRepository,
    private val englishWordRepository: EnglishWordRepository,
    private val userWordRelationRepository: UserWordRelationRepository
) {
    /**
     * ユーザーIDに関連付けられた単語のリストを取得
     */
    fun getUserWords(userId: Long): List<EnglishWord> {
        return userWordRelationRepository.findByUserId(userId).map { it.word }
    }
    
    /**
     * ユーザーと単語を関連付ける
     */
    @Transactional
    fun addWordToUser(userId: Long, wordId: Long): UserWordRelation? {
        val userOptional: Optional<User> = userRepository.findById(userId)
        val wordOptional: Optional<EnglishWord> = englishWordRepository.findById(wordId)
        
        if (userOptional.isPresent && wordOptional.isPresent) {
            val user = userOptional.get()
            val word = wordOptional.get()
            
            // 既に関連が存在するか確認
            val existingRelation = userWordRelationRepository.findByUserIdAndWordId(userId, wordId)
            if (existingRelation != null) {
                return existingRelation
            }
            
            // 新しい関連を作成
            val relation = UserWordRelation(
                user = user,
                word = word
            )
            return userWordRelationRepository.save(relation)
        }
        return null
    }
    
    /**
     * ユーザーと単語の関連を解除
     */
    @Transactional
    fun removeWordFromUser(userId: Long, wordId: Long): Boolean {
        val relation = userWordRelationRepository.findByUserIdAndWordId(userId, wordId)
        if (relation != null) {
            userWordRelationRepository.delete(relation)
            return true
        }
        return false
    }
    
    /**
     * ユーザーが特定の単語を持っているか確認
     */
    fun hasUserWord(userId: Long, wordId: Long): Boolean {
        return userWordRelationRepository.findByUserIdAndWordId(userId, wordId) != null
    }
} 