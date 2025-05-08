package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.dto.UserDto
import com.example.entity.AccessToken
import com.example.entity.User
import com.example.repository.AccessTokenRepository
import com.example.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class UserService(
    private val userRepository: UserRepository,
    private val accessTokenRepository: AccessTokenRepository,
    private val sessionService: SessionService
) {

    /**
     * JWT からユーザー情報を取得または作成し、最終ログイン日時を更新する
     */
    @Transactional
    fun getOrCreateUser(jwt: DecodedJWT): User {
        val auth0Id = jwt.subject // Auth0のユーザーID（sub）
        val email = jwt.getClaim("email").asString() ?: throw RuntimeException("メールアドレスが取得できません")
        val name = jwt.getClaim("name").asString()
        
        val now = LocalDateTime.now()
        val user = userRepository.findByAuth0Id(auth0Id).orElseGet {
            // 新規ユーザーを作成
            val newUser = User(
                auth0Id = auth0Id,
                email = email,
                name = name,
                lastLoginedAt = now,
                createdAt = now,
                updatedAt = now
            )
            userRepository.save(newUser)
        }
        
        // 最終ログイン日時を更新
        user.lastLoginedAt = LocalDateTime.now()
        user.updatedAt = LocalDateTime.now()
        return userRepository.save(user)
    }
    
    /**
     * アクセストークンを保存する
     */
    @Transactional
    fun saveAccessToken(user: User, accessToken: String, expiresIn: Int): AccessToken {
        // 有効期限を計算（現在時刻 + 有効期限秒数）
        val expiresAt = LocalDateTime.now().plusSeconds(expiresIn.toLong())
        
        val token = AccessToken(
            user = user,
            token = accessToken,
            expiresAt = expiresAt
        )
        
        return accessTokenRepository.save(token)
    }
    
    /**
     * ユーザーの最新のアクセストークンを取得する
     */
    fun getLatestAccessToken(user: User): AccessToken? {
        return accessTokenRepository.findFirstByUserOrderByCreatedAtDesc(user).orElse(null)
    }

    fun getCurrentUser(request: HttpServletRequest): UserDto {
        // セッションからユーザー情報を取得
        val session = sessionService.getCurrentUser(request)
            ?: throw Exception("ユーザーが認証されていません")
        
        // セッションからIDトークンが取得できない場合は、Auth0IDをそのまま使用
        if (session.idToken.isBlank()) {
            // IDトークンが無い場合は、Auth0IDと他の情報からユーザーDTOを生成
            return UserDto(
                id = session.auth0Id,
                name = session.name ?: "Guest",
                email = session.email,
                picture = null
            )
        }
        
        // IDトークンが存在する場合はJWTをデコード
        val decodedToken = decodeIdToken(session.idToken)
        
        return UserDto(
            id = decodedToken.claims["sub"]?.asString() ?: session.auth0Id,
            name = decodedToken.claims["name"]?.asString() ?: session.name ?: "Guest",
            email = decodedToken.claims["email"]?.asString() ?: session.email,
            picture = decodedToken.claims["picture"]?.asString()
        )
    }
    
    private fun decodeIdToken(idToken: String): DecodedJWT {
        return JWT.decode(idToken)
    }
} 