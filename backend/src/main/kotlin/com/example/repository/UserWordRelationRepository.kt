package com.example.repository

import com.example.entity.UserWordRelation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserWordRelationRepository : JpaRepository<UserWordRelation, Long> {
    
    // ユーザーIDに基づいて関連する単語を取得
    @Query("SELECT uwr FROM UserWordRelation uwr WHERE uwr.user.id = :userId")
    fun findByUserId(@Param("userId") userId: Long): List<UserWordRelation>
    
    // ユーザーIDと単語IDに基づいて関連を検索
    @Query("SELECT uwr FROM UserWordRelation uwr WHERE uwr.user.id = :userId AND uwr.word.id = :wordId")
    fun findByUserIdAndWordId(@Param("userId") userId: Long, @Param("wordId") wordId: Long): UserWordRelation?
    
    // ユーザーと単語の関連を削除
    fun deleteByUserIdAndWordId(userId: Long, wordId: Long)
} 