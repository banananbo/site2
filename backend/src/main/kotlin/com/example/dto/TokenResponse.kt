package com.example.dto

data class TokenResponse(
    val idToken: String,
    val accessToken: String,
    val expiresIn: Int,
    val tokenType: String
) 