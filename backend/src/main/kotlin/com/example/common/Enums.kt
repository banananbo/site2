package com.example.common

/**
 * 翻訳ステータスの列挙型
 */
enum class TranslationStatus {
    PENDING,    // 未処理
    PROCESSING, // 処理中
    COMPLETED,  // 完了
    ERROR       // エラー
}

/**
 * 難易度レベルの列挙型
 */
enum class DifficultyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    NATIVE
} 