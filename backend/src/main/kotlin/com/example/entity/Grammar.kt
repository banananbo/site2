package com.example.entity

import com.example.common.DifficultyLevel
import java.time.LocalDateTime
import jakarta.persistence.*

/**
 * 文法パターンエンティティ
 */
@Entity
@Table(name = "grammars")
data class Grammar(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, unique = true)
    val pattern: String,
    
    @Column(columnDefinition = "TEXT")
    val explanation: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column
    val level: DifficultyLevel = DifficultyLevel.INTERMEDIATE,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @ManyToMany(mappedBy = "grammars")
    val sentences: List<Sentence> = emptyList()
) 