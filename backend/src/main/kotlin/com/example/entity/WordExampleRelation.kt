package com.example.entity

import jakarta.persistence.*

@Entity
@Table(name = "word_example_relations")
class WordExampleRelation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "word_id", nullable = false)
    val wordId: Long,
    
    @Column(name = "example_id", nullable = false)
    val exampleId: Long
) 