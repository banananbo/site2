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
    var updatedAt: LocalDateTime = LocalDateTime.now()
) 