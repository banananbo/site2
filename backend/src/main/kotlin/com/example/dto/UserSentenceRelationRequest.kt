package com.example.dto

/**
 * ユーザーとセンテンスの関連情報のリクエストクラス
 */
data class UserSentenceRelationRequest(
    val userId: Long,
    val sentenceId: Long
) 