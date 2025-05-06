package com.example.config

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.slf4j.LoggerFactory

@Configuration
class FlywayConfig {
    private val logger = LoggerFactory.getLogger(FlywayConfig::class.java)
    
    @Bean
    fun flywayMigrationStrategy(): FlywayMigrationStrategy {
        return FlywayMigrationStrategy { flyway ->
            try {
                // データベースを完全にクリーンアップ
                val cleanResult = flyway.clean()
                logger.info("Database cleaned: {}", cleanResult)
                
                // 修復を実行
                val repairResult = flyway.repair()
                logger.info("Database repaired: {}", repairResult)
                
                // マイグレーション実行
                val migrateResult = flyway.migrate()
                logger.info("Migration completed: {}", migrateResult)
            } catch (e: Exception) {
                logger.error("Migration failed: ", e)
                throw e
            }
        }
    }
} 