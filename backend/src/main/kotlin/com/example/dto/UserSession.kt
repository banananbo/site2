package com.example.dto

import java.io.Serializable

data class UserSession(
    val userId: Long,
    val auth0Id: String,
    val email: String,
    val name: String?
) : Serializable 