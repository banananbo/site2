package com.example.service

import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.service.OpenAiService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.time.Duration

/**
 * OpenAIとの連携を行うサービス
 */
@Service
class OpenaiService(
    @Value("\${openai.api.key}") private val apiKey: String,
    @Value("\${openai.model}") private val model: String,
    @Value("\${openai.timeout}") private val timeout: Long,
    private val apiLogService: ApiLogService
) {
    private val logger = LoggerFactory.getLogger(OpenaiService::class.java)
    private val openAiService = OpenAiService(apiKey, Duration.ofSeconds(timeout))
    private val gson = Gson()
    
    /**
     * 英文を日本語に翻訳する
     */
    fun translateToJapanese(text: String): String? {
        try {
            // APIログ作成
            val logRequest = """
                {
                  "text": "$text",
                  "targetLanguage": "Japanese"
                }
            """.trimIndent()
            val apiLog = apiLogService.createRequestLog(
                apiName = "OpenAI",
                endpoint = "Translation",
                requestBody = logRequest
            )
            
            val prompt = "Translate the following English text to Japanese: \"$text\""
            
            val completionRequest = CompletionRequest.builder()
                .model(model)
                .prompt(prompt)
                .maxTokens(300)
                .temperature(0.3)
                .build()
            
            val completion = openAiService.createCompletion(completionRequest)
            val translation = completion.choices[0].text.trim()
            
            // APIログ更新
            apiLogService.updateWithResponse(
                apiLog = apiLog,
                responseBody = """{"translation": "$translation"}""",
                successful = true
            )
            
            return translation
        } catch (e: Exception) {
            logger.error("Error translating text: $text", e)
            return null
        }
    }
    
    /**
     * センテンスから単語、イディオム、文法パターンを抽出
     */
    fun extractElementsFromSentence(text: String): ExtractionResult {
        try {
            // APIログ作成
            val logRequest = """{"text": "$text"}"""
            val apiLog = apiLogService.createRequestLog(
                apiName = "OpenAI",
                endpoint = "ExtractElements",
                requestBody = logRequest
            )
            
            val prompt = """
                Please analyze the following English sentence and extract:
                1. Individual words (exclude common articles, prepositions, etc.)
                2. Idioms or phrasal expressions
                3. Grammar patterns used
                
                Format the response as a JSON object with the following structure:
                {
                  "words": ["word1", "word2", ...],
                  "idioms": ["idiom1", "idiom2", ...],
                  "grammarPatterns": ["pattern1", "pattern2", ...]
                }
                
                Sentence: "$text"
            """.trimIndent()
            
            val completionRequest = CompletionRequest.builder()
                .model(model)
                .prompt(prompt)
                .maxTokens(500)
                .temperature(0.2)
                .build()
            
            val completion = openAiService.createCompletion(completionRequest)
            val responseText = completion.choices[0].text.trim()
            
            try {
                val result = gson.fromJson(responseText, ExtractionResult::class.java)
                
                // APIログ更新
                apiLogService.updateWithResponse(
                    apiLog = apiLog,
                    responseBody = responseText,
                    successful = true
                )
                
                return result
            } catch (e: JsonSyntaxException) {
                logger.error("Failed to parse AI response: $responseText", e)
                
                // APIログ更新（エラー）
                apiLogService.updateWithResponse(
                    apiLog = apiLog,
                    responseBody = responseText,
                    successful = false,
                    errorMessage = "JSON解析エラー: ${e.message}"
                )
                
                return ExtractionResult(emptyList(), emptyList(), emptyList())
            }
        } catch (e: Exception) {
            logger.error("Error calling OpenAI API", e)
            return ExtractionResult(emptyList(), emptyList(), emptyList())
        }
    }
    
    /**
     * イディオムの意味を取得
     */
    fun getIdiomMeaning(idiom: String): String? {
        try {
            // APIログ作成
            val logRequest = """{"idiom": "$idiom"}"""
            val apiLog = apiLogService.createRequestLog(
                apiName = "OpenAI",
                endpoint = "IdiomMeaning",
                requestBody = logRequest
            )
            
            val prompt = "What is the meaning of the idiom or phrase: \"$idiom\"? Provide a concise explanation in Japanese."
            
            val completionRequest = CompletionRequest.builder()
                .model(model)
                .prompt(prompt)
                .maxTokens(100)
                .temperature(0.3)
                .build()
            
            val completion = openAiService.createCompletion(completionRequest)
            val meaning = completion.choices[0].text.trim()
            
            // APIログ更新
            apiLogService.updateWithResponse(
                apiLog = apiLog,
                responseBody = """{"meaning": "$meaning"}""",
                successful = true
            )
            
            return meaning
        } catch (e: Exception) {
            logger.error("Error getting idiom meaning for: $idiom", e)
            return null
        }
    }
    
    /**
     * イディオムの詳細説明を取得
     */
    fun getIdiomExplanation(idiom: String): String? {
        try {
            // APIログ作成
            val logRequest = """{"idiom": "$idiom"}"""
            val apiLog = apiLogService.createRequestLog(
                apiName = "OpenAI",
                endpoint = "IdiomExplanation",
                requestBody = logRequest
            )
            
            val prompt = """
                Explain the idiom or phrase: "$idiom"
                Include:
                - Literal meaning
                - Figurative meaning
                - Example usage
                - Cultural context if relevant
                Answer in Japanese.
            """.trimIndent()
            
            val completionRequest = CompletionRequest.builder()
                .model(model)
                .prompt(prompt)
                .maxTokens(200)
                .temperature(0.3)
                .build()
            
            val completion = openAiService.createCompletion(completionRequest)
            val explanation = completion.choices[0].text.trim()
            
            // APIログ更新
            apiLogService.updateWithResponse(
                apiLog = apiLog,
                responseBody = """{"explanation": "$explanation"}""",
                successful = true
            )
            
            return explanation
        } catch (e: Exception) {
            logger.error("Error getting idiom explanation for: $idiom", e)
            return null
        }
    }
    
    /**
     * 文法パターンの説明を取得
     */
    fun getGrammarExplanation(pattern: String): String {
        try {
            // APIログ作成
            val logRequest = """{"pattern": "$pattern"}"""
            val apiLog = apiLogService.createRequestLog(
                apiName = "OpenAI",
                endpoint = "GrammarExplanation",
                requestBody = logRequest
            )
            
            val prompt = """
                Explain the English grammar pattern: "$pattern"
                Include:
                - Structure explanation
                - Usage rules
                - Example sentences
                Answer in Japanese.
            """.trimIndent()
            
            val completionRequest = CompletionRequest.builder()
                .model(model)
                .prompt(prompt)
                .maxTokens(200)
                .temperature(0.3)
                .build()
            
            val completion = openAiService.createCompletion(completionRequest)
            val explanation = completion.choices[0].text.trim()
            
            // APIログ更新
            apiLogService.updateWithResponse(
                apiLog = apiLog,
                responseBody = """{"explanation": "$explanation"}""",
                successful = true
            )
            
            return explanation
        } catch (e: Exception) {
            logger.error("Error getting grammar explanation for: $pattern", e)
            return "文法パターンの説明を取得できませんでした。"
        }
    }
}

/**
 * センテンス解析結果
 */
data class ExtractionResult(
    val words: List<String>,
    val idioms: List<String>,
    val grammarPatterns: List<String>
) 