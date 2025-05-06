package com.example.dto

/**
 * ユーザーとセンテンスの関連情報のレスポンスクラス
 */
data class UserSentenceRelationResponse(
    val id: Long,
    val userId: Long,
    val sentenceId: Long,
    val sentenceText: String,
    val translation: String?
) 