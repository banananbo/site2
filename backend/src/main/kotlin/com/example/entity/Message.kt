package com.example.entity

import jakarta.persistence.*

@Entity
@Table(name = "messages")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val content: String,
    
    @Column(name = "user_id", nullable = false)
    val userId: Long = 1
) 