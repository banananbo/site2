package com.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
        // 各パラメータをURLエンコードして、余分なスペースを取り除く
        val encodedRedirectUri = URLEncoder.encode(redirectUri.trim(), StandardCharsets.UTF_8.toString())
        val encodedAudience = URLEncoder.encode(audience.trim(), StandardCharsets.UTF_8.toString())
        
        // 一行でURLを構築して余分なスペースが混入しないようにする
        return "https://${domain.trim()}/authorize?response_type=code&client_id=${clientId.trim()}&redirect_uri=$encodedRedirectUri&audience=$encodedAudience&scope=openid%20profile%20email"
    }
} 