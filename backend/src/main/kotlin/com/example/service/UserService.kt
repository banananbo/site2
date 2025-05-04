package com.example.service

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.entity.AccessToken
import com.example.entity.User
import com.example.repository.AccessTokenRepository
import com.example.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class UserService(
    private val userRepository: UserRepository,
    private val accessTokenRepository: AccessTokenRepository
) {

    /**
     * JWT からユーザー情報を取得または作成し、最終ログイン日時を更新する
     */
    @Transactional
    fun getOrCreateUser(jwt: DecodedJWT): User {
        val auth0Id = jwt.subject // Auth0のユーザーID（sub）
        val email = jwt.getClaim("email").asString() ?: throw RuntimeException("メールアドレスが取得できません")
        val name = jwt.getClaim("name").asString()
        
        val user = userRepository.findByAuth0Id(auth0Id).orElseGet {
            // 新規ユーザーを作成
            val newUser = User(
                auth0Id = auth0Id,
                email = email,
                name = name
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
} 