package com.example.dto

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val picture: String? = null
) 