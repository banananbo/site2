package com.example.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Auth0TokenResponse(
    @JsonProperty("id_token")
    val idToken: String,
    
    @JsonProperty("access_token")
    val accessToken: String,
    
    @JsonProperty("expires_in")
    val expiresIn: Int,
    
    @JsonProperty("token_type")
    val tokenType: String
) 