package com.example.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(unique = true, nullable = false)
    val auth0Id: String,
    
    @Column(nullable = false)
    val email: String,
    
    @Column(nullable = true)
    var name: String? = null,
    
    @Column(nullable = false)
    var lastLoginedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val userWordRelations: MutableList<UserWordRelation> = mutableListOf()
) {
    // ユーザーに関連付けられた単語のリスト
    val words: List<EnglishWord>
        get() = userWordRelations.map { it.word }
        
    // ユーザーと単語を関連付けるメソッド
    fun addWord(word: EnglishWord): UserWordRelation {
        val relation = UserWordRelation(user = this, word = word)
        userWordRelations.add(relation)
        return relation
    }
    
    // ユーザーと単語の関連付けを解除するメソッド
    fun removeWord(word: EnglishWord) {
        userWordRelations.removeIf { it.word.id == word.id }
    }
} 