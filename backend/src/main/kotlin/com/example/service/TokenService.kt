package com.example.service

import com.example.config.Auth0Config
import com.example.dto.Auth0TokenResponse
import com.example.dto.TokenResponse
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import java.security.interfaces.RSAPublicKey
import org.springframework.web.client.HttpClientErrorException
import java.net.URL
import com.auth0.jwk.JwkProviderBuilder
import java.util.concurrent.TimeUnit
import jakarta.servlet.http.HttpServletRequest
import org.springframework.transaction.annotation.Transactional

@Service
class TokenService(
    private val auth0Config: Auth0Config,
    private val userService: UserService,
    private val sessionService: SessionService
) {

    private val restTemplate = RestTemplate()
    
    /**
     * Auth0のトークンエンドポイントからトークンを取得し、ユーザー登録とセッション作成を行う
     */
    @Transactional
    fun processCodeAndCreateSession(code: String, request: HttpServletRequest): Auth0TokenResponse {
        // Auth0からトークンを取得
        val response = getTokenFromAuth0(code)
        
        // JWTを検証
        val jwt = verifyToken(response.idToken)
        
        // ユーザー情報を取得または作成し、最終ログイン日時を更新
        val user = userService.getOrCreateUser(jwt)
        
        // アクセストークンを保存
        userService.saveAccessToken(user, response.accessToken, response.expiresIn)
        
        // セッションを作成（ログイン状態にする）- IDトークンも保存
        sessionService.createUserSession(request, user, response.idToken)
        
        return response
    }
    
    /**
     * Auth0のトークンエンドポイントからIDトークンを取得する
     */
    fun getTokenFromAuth0(code: String): Auth0TokenResponse {
        val url = "https://${auth0Config.domain}/oauth/token"
        
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        
        val body = LinkedMultiValueMap<String, String>()
        body.add("grant_type", "authorization_code")
        body.add("client_id", auth0Config.clientId)
        body.add("client_secret", auth0Config.clientSecret)
        body.add("code", code)
        body.add("redirect_uri", auth0Config.redirectUri)
        
        val request = HttpEntity(body, headers)
        
        try {
            val response = restTemplate.postForObject(url, request, Auth0TokenResponse::class.java)
            return response ?: throw RuntimeException("Auth0からのトークン取得に失敗しました")
        } catch (e: HttpClientErrorException) {
            throw RuntimeException("Auth0からのトークン取得中にエラーが発生しました: ${e.responseBodyAsString}", e)
        } catch (e: Exception) {
            throw RuntimeException("Auth0からのトークン取得中にエラーが発生しました", e)
        }
    }
    
    /**
     * IDトークンの署名を検証する
     */
    fun verifyToken(idToken: String): DecodedJWT {
        try {
            // JWKプロバイダーの設定
            val jwkProvider = JwkProviderBuilder(URL("https://${auth0Config.domain}/.well-known/jwks.json"))
                .cached(10, 24, TimeUnit.HOURS) // キャッシュ設定
                .rateLimited(10, 1, TimeUnit.MINUTES) // レート制限
                .build()
            
            // トークンからキーIDを取得
            val jwt = JWT.decode(idToken)
            val keyId = jwt.keyId
            
            // 公開鍵を取得
            val jwk = jwkProvider.get(keyId)
            val publicKey = jwk.publicKey as RSAPublicKey
            
            // アルゴリズムでトークンを検証
            val algorithm = Algorithm.RSA256(publicKey, null)
            val verifier = JWT.require(algorithm)
                .withIssuer("https://${auth0Config.domain}/")
                .withAudience(auth0Config.clientId)
                .build()
            
            return verifier.verify(idToken)
        } catch (e: JWTVerificationException) {
            throw RuntimeException("トークンの検証に失敗しました", e)
        } catch (e: Exception) {
            throw RuntimeException("トークンの検証中にエラーが発生しました", e)
        }
    }
    
    /**
     * Auth0から取得したトークンをフロントエンド用のレスポンスに変換する
     */
    fun convertToTokenResponse(auth0Response: Auth0TokenResponse): TokenResponse {
        return TokenResponse(
            idToken = auth0Response.idToken,
            accessToken = auth0Response.accessToken,
            expiresIn = auth0Response.expiresIn,
            tokenType = auth0Response.tokenType
        )
    }
} 