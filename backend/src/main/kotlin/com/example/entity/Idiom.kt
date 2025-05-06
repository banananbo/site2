package com.example.entity

import java.time.LocalDateTime
import jakarta.persistence.*

/**
 * イディオム（慣用句）エンティティ
 */
@Entity
@Table(name = "idioms")
data class Idiom(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, unique = true)
    val phrase: String,
    
    @Column
    val meaning: String? = null,
    
    @Column
    val explanation: String? = null,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @ManyToMany(mappedBy = "idioms")
    val sentences: List<Sentence> = emptyList()
) 