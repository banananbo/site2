package com.example.job

import com.example.entity.TranslationStatus
import com.example.repository.EnglishWordRepository
import com.example.service.TranslationService
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.PlatformTransactionManager
import java.util.concurrent.TimeUnit

@Configuration
class TranslationJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val englishWordRepository: EnglishWordRepository,
    private val translationService: TranslationService
) {
    private val log = LoggerFactory.getLogger(javaClass)
    
    @Bean
    fun wordTranslationJob(wordTranslationStep: Step): Job {
        return JobBuilder("wordTranslationJob", jobRepository)
            .start(wordTranslationStep)
            .build()
    }
    
    @Bean
    fun wordTranslationStep(): Step {
        return StepBuilder("wordTranslationStep", jobRepository)
            .tasklet({ contribution, chunkContext ->
                log.info("翻訳バッチ処理を開始")
                
                val pendingWords = englishWordRepository.findByTranslationStatus(TranslationStatus.PENDING)
                
                log.info("未翻訳の単語数: {}", pendingWords.size)
                
                var successCount = 0
                var errorCount = 0
                
                pendingWords.forEach { word ->
                    try {
                        if (translationService.translateWord(word)) {
                            successCount++
                        } else {
                            errorCount++
                        }
                        // APIレート制限を考慮して待機 (3秒)
                        TimeUnit.SECONDS.sleep(3)
                    } catch (e: Exception) {
                        log.error("単語の翻訳中にエラーが発生しました: {}", word.word, e)
                        errorCount++
                        // エラー時も継続処理するため、例外は捕捉するだけ
                    }
                }
                
                log.info("翻訳バッチ処理を完了: 成功={}, 失敗={}", successCount, errorCount)
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
    
    // 2分ごとに実行（テスト中はあまり頻繁に実行しないように）
    @Scheduled(fixedRate = 120000)
    fun runTranslationJob() {
        log.info("定期的な翻訳ジョブを開始")
        
        try {
            // 翻訳が必要な単語があるか確認
            val pendingCount = englishWordRepository.countByTranslationStatus(TranslationStatus.PENDING)
            
            if (pendingCount > 0) {
                log.info("{}個の未翻訳単語があります。翻訳処理を実行します。", pendingCount)
                
                // 直接サービスを呼び出して処理（小規模アプリ向け）
                val pendingWords = englishWordRepository.findByTranslationStatus(TranslationStatus.PENDING)
                
                var successCount = 0
                var errorCount = 0
                
                pendingWords.forEach { word ->
                    try {
                        if (translationService.translateWord(word)) {
                            successCount++
                        } else {
                            errorCount++
                        }
                        // APIレート制限を考慮して待機 (3秒)
                        TimeUnit.SECONDS.sleep(3)
                    } catch (e: Exception) {
                        log.error("単語の翻訳中にエラーが発生しました: {}", word.word, e)
                        errorCount++
                        // エラー時も継続処理するため、例外は捕捉するだけ
                    }
                }
                
                log.info("翻訳処理を完了: 成功={}, 失敗={}", successCount, errorCount)
            } else {
                log.info("未翻訳の単語はありません")
            }
        } catch (e: Exception) {
            log.error("翻訳ジョブの実行中にエラーが発生しました", e)
        }
    }
} 