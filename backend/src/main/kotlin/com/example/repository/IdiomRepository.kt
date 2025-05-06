package com.example.repository

import com.example.entity.Idiom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IdiomRepository : JpaRepository<Idiom, Long> {
    fun findByPhrase(phrase: String): Idiom?
    fun findByPhraseContaining(phrase: String): List<Idiom>
    fun existsByPhrase(phrase: String): Boolean
} 