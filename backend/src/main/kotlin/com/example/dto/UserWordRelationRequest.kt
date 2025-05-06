package com.example.dto

/**
 * ユーザーと単語の関連情報のリクエストクラス
 */
data class UserWordRelationRequest(
    val userId: Long,
    val wordId: Long
) 