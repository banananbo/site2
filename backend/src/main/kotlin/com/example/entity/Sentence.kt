package com.example.entity

import com.example.common.DifficultyLevel
import com.example.common.TranslationStatus
import java.time.LocalDateTime
import jakarta.persistence.*

/**
 * センテンス（文）エンティティ
 */
@Entity
@Table(name = "sentences")
data class Sentence(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val text: String,
    
    @Column
    var translation: String? = null,
    
    @Column
    val note: String? = null,
    
    @Column
    val source: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val difficulty: DifficultyLevel = DifficultyLevel.INTERMEDIATE,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "translation_status", nullable = false)
    var translationStatus: TranslationStatus = TranslationStatus.PENDING,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @ManyToMany
    @JoinTable(
        name = "sentence_word_relations",
        joinColumns = [JoinColumn(name = "sentence_id")],
        inverseJoinColumns = [JoinColumn(name = "word_id")]
    )
    val words: List<EnglishWord> = emptyList(),
    
    @ManyToMany
    @JoinTable(
        name = "sentence_idiom_relations",
        joinColumns = [JoinColumn(name = "sentence_id")],
        inverseJoinColumns = [JoinColumn(name = "idiom_id")]
    )
    val idioms: List<Idiom> = emptyList(),
    
    @ManyToMany
    @JoinTable(
        name = "sentence_grammar_relations",
        joinColumns = [JoinColumn(name = "sentence_id")],
        inverseJoinColumns = [JoinColumn(name = "grammar_id")]
    )
    val grammars: List<Grammar> = emptyList()
) 