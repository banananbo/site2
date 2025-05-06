package com.example.entity

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "english_words")
data class EnglishWord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, unique = true)
    val word: String,
    
    @Column
    var meaning: String? = null,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Enumerated(EnumType.STRING)
    @Column(name = "translation_status")
    var translationStatus: TranslationStatus = TranslationStatus.PENDING,
    
    @ManyToMany
    @JoinTable(
        name = "word_example_relations",
        joinColumns = [JoinColumn(name = "wordId")],
        inverseJoinColumns = [JoinColumn(name = "exampleId")]
    )
    val examples: List<WordExample> = emptyList()
)

enum class TranslationStatus {
    PENDING, COMPLETED, ERROR
} 