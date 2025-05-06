package com.example.repository

import com.example.entity.UserSentenceRelation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserSentenceRelationRepository : JpaRepository<UserSentenceRelation, Long> {
    
    // ユーザーIDに基づいて関連するセンテンスを取得
    @Query("SELECT usr FROM UserSentenceRelation usr WHERE usr.user.id = :userId")
    fun findByUserId(@Param("userId") userId: Long): List<UserSentenceRelation>
    
    // ユーザーIDとセンテンスIDに基づいて関連を検索
    @Query("SELECT usr FROM UserSentenceRelation usr WHERE usr.user.id = :userId AND usr.sentence.id = :sentenceId")
    fun findByUserIdAndSentenceId(@Param("userId") userId: Long, @Param("sentenceId") sentenceId: Long): UserSentenceRelation?
    
    // ユーザーとセンテンスの関連を削除
    fun deleteByUserIdAndSentenceId(userId: Long, sentenceId: Long)
} 