package com.example.entity

import jakarta.persistence.*

@Entity
@Table(name = "word_examples")
class WordExample(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val example: String,
    
    @Column
    val translation: String? = null,
    
    @Column
    val note: String? = null,
    
    @Column
    val source: String? = null
) 