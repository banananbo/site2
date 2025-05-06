package com.example.dto

/**
 * ユーザーと単語の関連情報のレスポンスクラス
 */
data class UserWordRelationResponse(
    val id: Long,
    val userId: Long,
    val wordId: Long,
    val wordText: String
) 