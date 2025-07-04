package com.example.repository

import com.example.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByAuth0Id(auth0Id: String): Optional<User>
    fun existsByAuth0Id(auth0Id: String): Boolean
} 