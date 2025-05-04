package com.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class Auth0Config {
    @Value("\${auth0.domain}")
    lateinit var domain: String

    @Value("\${auth0.clientId}")
    lateinit var clientId: String

    @Value("\${auth0.clientSecret}")
    lateinit var clientSecret: String

    @Value("\${auth0.audience}")
    lateinit var audience: String

    @Value("\${auth0.redirectUri}")
    lateinit var redirectUri: String

    fun getAuthorizeUrl(): String {
        return "https://$domain/authorize" +
                "?response_type=code" +
                "&client_id=$clientId" +
                "&redirect_uri=$redirectUri" +
                "&audience=$audience" +
                "&scope=openid profile email"
    }
} 