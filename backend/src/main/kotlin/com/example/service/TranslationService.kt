package com.example.service

import com.example.entity.ApiLog
import com.example.entity.EnglishWord
import com.example.common.TranslationStatus
import com.example.repository.EnglishWordRepository
import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.service.OpenAiService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import retrofit2.adapter.rxjava2.HttpException
import java.util.concurrent.TimeUnit
import com.google.gson.Gson

@Service
class TranslationService(
    private val openAiService: OpenAiService,
    private val englishWordRepository: EnglishWordRepository,
    private val apiLogService: ApiLogService,
    private val wordExampleService: WordExampleService
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val gson = Gson()
    
    @Value("\${openai.model:gpt-3.5-turbo-instruct}")
    private lateinit var model: String
    
    @Transactional
    fun translateWord(word: EnglishWord): Boolean {
        try {
            log.info("翻訳を開始: {}", word.word)
            
            val prompt = """
                あなたは英語教師です。学習者向けに英単語の意味と例文を提供します。
                
                英単語「${word.word}」の日本語の意味と、その単語を使った英語の例文を3つ提供してください。
                それぞれの例文には日本語訳も付けてください。
                以下の形式でJSON形式で返してください：
                {
                  "meaning": "日本語の意味",
                  "examples": [
                    {"example": "英語の例文1", "translation": "日本語訳1"},
                    {"example": "英語の例文2", "translation": "日本語訳2"},
                    {"example": "英語の例文3", "translation": "日本語訳3"}
                  ]
                }
                
                JSONフォーマット以外の文章は含めないでください。
            """.trimIndent()
            
            // リクエストオブジェクトを作成
            val request = CompletionRequest.builder()
                .model(model)
                .prompt(prompt)
                .temperature(0.7)
                .maxTokens(200)
                .topP(1.0)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .build()
            
            // リクエストをJSON形式に変換してログに保存
            val requestJson = gson.toJson(request)
            val apiLog = apiLogService.createRequestLog(
                apiName = "OpenAI",
                endpoint = "completions",
                requestBody = requestJson,
                wordId = word.id
            )
            
            // リトライロジック
            var retryCount = 0
            val maxRetries = 3
            var success = false
            var latestResponse: String? = null
            var latestError: String? = null
            
            while (retryCount < maxRetries && !success) {
                try {
                    val response = openAiService.createCompletion(request)
                    latestResponse = gson.toJson(response)
                    
                    if (response.choices.isNotEmpty()) {
                        val content = response.choices[0].text
                        
                        // JSON形式の応答から意味と例文を抽出
                        val meaningRegex = "\"meaning\":\\s*\"([^\"]+)\"".toRegex()
                        val examplesRegex = "\"examples\":\\s*\\[(.*?)\\]".toRegex(RegexOption.DOT_MATCHES_ALL)
                        
                        val meaningMatch = meaningRegex.find(content)
                        val examplesMatch = examplesRegex.find(content)
                        
                        word.meaning = meaningMatch?.groupValues?.get(1)
                        
                        // 既存の例文を削除して新しい例文を追加
                        if (word.id != null) {
                            wordExampleService.removeAllExamplesFromWord(word.id)
                        }
                        
                        // 例文を抽出して保存
                        if (examplesMatch != null && word.id != null) {
                            val examplesJson = examplesMatch.groupValues[1]
                            val examplePattern = "\\{\\s*\"example\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"translation\"\\s*:\\s*\"([^\"]+)\"\\s*\\}".toRegex()
                            val exampleMatches = examplePattern.findAll(examplesJson)
                            
                            exampleMatches.forEach { match ->
                                val exampleText = match.groupValues[1]
                                val translationText = match.groupValues[2]
                                
                                wordExampleService.addExampleToWord(
                                    wordId = word.id,
                                    example = exampleText,
                                    translation = translationText,
                                    source = "OpenAI"
                                )
                            }
                        }
                        
                        word.translationStatus = TranslationStatus.COMPLETED
                        
                        englishWordRepository.save(word)
                        log.info("翻訳完了: {}", word.word)
                        
                        success = true
                    } else {
                        latestError = "OpenAIからの応答が空です"
                        log.warn("OpenAIからの応答が空です。リトライ {}/{}", retryCount + 1, maxRetries)
                        retryCount++
                        if (retryCount < maxRetries) {
                            // バックオフ戦略: 待機時間を徐々に増やす
                            TimeUnit.SECONDS.sleep((2L * (retryCount + 1)))
                        }
                    }
                } catch (e: HttpException) {
                    val statusCode = e.code()
                    latestError = "HTTPエラー: ${statusCode}, メッセージ: ${e.message()}"
                    
                    if (statusCode == 429) {
                        log.warn("レート制限に達しました（HTTP 429）。リトライ {}/{}", retryCount + 1, maxRetries)
                        retryCount++
                        if (retryCount < maxRetries) {
                            // レート制限エラー時は長めに待機
                            TimeUnit.SECONDS.sleep((5L * (retryCount + 1)))
                        }
                    } else {
                        throw e // 他のHTTPエラーは再スロー
                    }
                } catch (e: Exception) {
                    latestError = "例外: ${e.javaClass.simpleName}, メッセージ: ${e.message}"
                    throw e
                }
            }
            
            // APIログを更新
            apiLogService.updateWithResponse(
                apiLog = apiLog,
                responseBody = latestResponse,
                successful = success,
                errorMessage = if (!success) latestError else null
            )
            
            if (!success) {
                word.translationStatus = TranslationStatus.ERROR
                englishWordRepository.save(word)
                log.error("最大リトライ回数に達しました。翻訳は失敗しました: {}", word.word)
                return false
            }
            
            return true
        } catch (e: Exception) {
            log.error("翻訳エラー: {}", word.word, e)
            word.translationStatus = TranslationStatus.ERROR
            englishWordRepository.save(word)
            return false
        }
    }
} 