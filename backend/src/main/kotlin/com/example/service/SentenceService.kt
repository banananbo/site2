package com.example.service

import com.example.common.DifficultyLevel
import com.example.common.TranslationStatus
import com.example.dto.SentenceDto
import com.example.entity.Sentence
import com.example.entity.Idiom
import com.example.entity.Grammar
import com.example.repository.SentenceRepository
import com.example.repository.EnglishWordRepository
import com.example.repository.IdiomRepository
import com.example.repository.GrammarRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async

@Service
class SentenceService(
    private val sentenceRepository: SentenceRepository,
    private val englishWordRepository: EnglishWordRepository,
    private val idiomRepository: IdiomRepository,
    private val grammarRepository: GrammarRepository,
    private val englishWordService: EnglishWordService,
    private val openaiService: OpenaiService,
    private val entityManager: EntityManager
) {
    private val logger = LoggerFactory.getLogger(SentenceService::class.java)

    /**
     * センテンスを登録し、要素を抽出して関連付ける
     * 処理を即時返し、翻訳と要素抽出は非同期で行う
     */
    @Transactional
    fun registerSentence(
        text: String,
        translation: String? = null,
        note: String? = null,
        source: String? = null,
        difficulty: String = "INTERMEDIATE"
    ): SentenceDto {
        // 難易度レベルを解析
        val difficultyLevel = try {
            DifficultyLevel.valueOf(difficulty)
        } catch (e: IllegalArgumentException) {
            DifficultyLevel.INTERMEDIATE
        }
        
        // センテンスを作成して保存（PENDINGステータスで）
        val sentence = Sentence(
            text = text,
            translation = translation,
            note = note,
            source = source,
            difficulty = difficultyLevel,
            translationStatus = if (translation != null) TranslationStatus.COMPLETED else TranslationStatus.PENDING
        )
        
        val savedSentence = sentenceRepository.save(sentence)
        
        // 非同期で翻訳と要素抽出処理を開始
        processTranslationAndExtraction(savedSentence.id!!, text)
        
        return SentenceDto.fromEntity(savedSentence)
    }
    
    /**
     * 非同期で翻訳と要素抽出処理を行う
     */
    @Async("taskExecutor")
    @Transactional
    fun processTranslationAndExtraction(sentenceId: Long, text: String) {
        logger.info("センテンス処理を開始: ID={}", sentenceId)
        try {
            val sentence = sentenceRepository.findById(sentenceId).orElse(null) ?: run {
                logger.error("センテンスが見つかりません: ID={}", sentenceId)
                return
            }
            
            // 処理中に更新
            sentence.translationStatus = TranslationStatus.PROCESSING
            sentenceRepository.save(sentence)
            
            // 自動翻訳が未実行の場合は実行
            if (sentence.translation == null) {
                try {
                    val translation = openaiService.translateToJapanese(text)
                    if (translation != null) {
                        sentence.translation = translation
                        sentenceRepository.save(sentence)
                    }
                } catch (e: Exception) {
                    logger.error("自動翻訳に失敗しました: {}", text, e)
                }
            }
            
            // 要素抽出を行う
            extractAndLinkElements(sentenceId, text)
            
            logger.info("センテンス処理を完了: ID={}", sentenceId)
        } catch (e: Exception) {
            logger.error("センテンス処理中にエラーが発生しました: ID={}", sentenceId, e)
            try {
                val sentence = sentenceRepository.findById(sentenceId).orElse(null)
                if (sentence != null) {
                    sentence.translationStatus = TranslationStatus.ERROR
                    sentenceRepository.save(sentence)
                }
            } catch (ex: Exception) {
                logger.error("エラー状態の更新に失敗しました: ID={}", sentenceId, ex)
            }
        }
    }
    
    /**
     * センテンスから単語、イディオム、文法を抽出して関連付ける
     */
    @Transactional
    fun extractAndLinkElements(sentenceId: Long, text: String) {
        val sentence = sentenceRepository.findById(sentenceId).orElse(null) ?: return
        
        try {
            // OpenAI APIを使用して要素を抽出
            val extractionResult = openaiService.extractElementsFromSentence(text)
            
            // 抽出された単語を処理
            extractionResult.words.forEach { word ->
                val englishWordDto = englishWordService.registerWord(word)
                
                if (englishWordDto.id != null) {
                    // ネイティブSQLでリレーションテーブルに直接挿入
                    entityManager.createNativeQuery("""
                        INSERT IGNORE INTO sentence_word_relations (sentence_id, word_id)
                        VALUES (:sentenceId, :wordId)
                    """)
                    .setParameter("sentenceId", sentenceId)
                    .setParameter("wordId", englishWordDto.id)
                    .executeUpdate()
                }
            }
            
            // 抽出されたイディオムを処理
            extractionResult.idioms.forEach { phraseText ->
                val idiom = idiomRepository.findByPhrase(phraseText) ?: run {
                    val newIdiom = Idiom(
                        phrase = phraseText,
                        meaning = openaiService.getIdiomMeaning(phraseText),
                        explanation = openaiService.getIdiomExplanation(phraseText)
                    )
                    idiomRepository.save(newIdiom)
                }
                
                if (idiom.id != null) {
                    // ネイティブSQLでリレーションテーブルに直接挿入
                    entityManager.createNativeQuery("""
                        INSERT IGNORE INTO sentence_idiom_relations (sentence_id, idiom_id)
                        VALUES (:sentenceId, :idiomId)
                    """)
                    .setParameter("sentenceId", sentenceId)
                    .setParameter("idiomId", idiom.id)
                    .executeUpdate()
                }
            }
            
            // 抽出された文法パターンを処理
            extractionResult.grammarPatterns.forEach { patternText ->
                val grammar = grammarRepository.findByPattern(patternText) ?: run {
                    val newGrammar = Grammar(
                        pattern = patternText,
                        explanation = openaiService.getGrammarExplanation(patternText),
                        level = DifficultyLevel.INTERMEDIATE
                    )
                    grammarRepository.save(newGrammar)
                }
                
                if (grammar.id != null) {
                    // ネイティブSQLでリレーションテーブルに直接挿入
                    entityManager.createNativeQuery("""
                        INSERT IGNORE INTO sentence_grammar_relations (sentence_id, grammar_id)
                        VALUES (:sentenceId, :grammarId)
                    """)
                    .setParameter("sentenceId", sentenceId)
                    .setParameter("grammarId", grammar.id)
                    .executeUpdate()
                }
            }
            
            // 処理完了に更新
            sentence.translationStatus = TranslationStatus.COMPLETED
            sentenceRepository.save(sentence)
            
            // リレーションが更新されたのでエンティティをリフレッシュ
            entityManager.refresh(sentence)
        } catch (e: Exception) {
            logger.error("センテンス要素の抽出に失敗しました: $sentenceId", e)
            // エラー状態に更新
            sentence.translationStatus = TranslationStatus.ERROR
            sentenceRepository.save(sentence)
        }
    }
    
    /**
     * センテンスをIDで取得
     */
    @Transactional(readOnly = true)
    fun getSentenceById(id: Long): SentenceDto? {
        val sentence = sentenceRepository.findById(id).orElse(null) ?: return null
        return SentenceDto.fromEntity(sentence)
    }
    
    /**
     * すべてのセンテンスを取得
     */
    @Transactional(readOnly = true)
    fun getAllSentences(): List<SentenceDto> {
        return sentenceRepository.findAll().map { SentenceDto.fromEntity(it) }
    }
    
    /**
     * 難易度レベルでセンテンスを検索
     */
    @Transactional(readOnly = true)
    fun getSentencesByDifficulty(difficulty: String): List<SentenceDto> {
        val difficultyLevel = try {
            DifficultyLevel.valueOf(difficulty)
        } catch (e: IllegalArgumentException) {
            return emptyList()
        }
        
        return sentenceRepository.findByDifficulty(difficultyLevel)
            .map { SentenceDto.fromEntity(it) }
    }
    
    /**
     * センテンスを更新
     */
    @Transactional
    fun updateSentence(
        id: Long,
        text: String,
        translation: String?,
        note: String?,
        source: String?,
        difficulty: String
    ): SentenceDto? {
        val existingSentence = sentenceRepository.findById(id).orElse(null) ?: return null
        
        val difficultyLevel = try {
            DifficultyLevel.valueOf(difficulty)
        } catch (e: IllegalArgumentException) {
            existingSentence.difficulty
        }
        
        // 翻訳が指定されていない場合は自動翻訳を試みる
        val finalTranslation = if (translation.isNullOrBlank() && existingSentence.text != text) {
            try {
                openaiService.translateToJapanese(text)
            } catch (e: Exception) {
                logger.error("自動翻訳に失敗しました: $text", e)
                existingSentence.translation
            }
        } else {
            translation ?: existingSentence.translation
        }
        
        val updatedSentence = Sentence(
            id = existingSentence.id,
            text = text,
            translation = finalTranslation,
            note = note,
            source = source,
            difficulty = difficultyLevel,
            translationStatus = if (finalTranslation != null) TranslationStatus.COMPLETED else TranslationStatus.PENDING,
            createdAt = existingSentence.createdAt,
            updatedAt = LocalDateTime.now(),
            // 既存の関連を維持
            words = existingSentence.words,
            idioms = existingSentence.idioms,
            grammars = existingSentence.grammars
        )
        
        val savedSentence = sentenceRepository.save(updatedSentence)
        
        // テキストが変更された場合は要素を再抽出
        if (text != existingSentence.text && savedSentence.id != null) {
            // 関連を一度削除
            deleteAllRelations(savedSentence.id)
            // 要素を再抽出して関連付け（非同期処理）
            processTranslationAndExtraction(savedSentence.id, text)
        }
        
        return SentenceDto.fromEntity(savedSentence)
    }
    
    /**
     * センテンスを削除
     */
    @Transactional
    fun deleteSentence(id: Long): Boolean {
        if (!sentenceRepository.existsById(id)) {
            return false
        }
        
        // リレーションテーブルからの関連を削除
        deleteAllRelations(id)
        
        // センテンスを削除
        sentenceRepository.deleteById(id)
        return true
    }
    
    /**
     * センテンスの全ての関連を削除
     */
    @Transactional
    fun deleteAllRelations(sentenceId: Long) {
        // 単語との関連を削除
        entityManager.createNativeQuery(
            "DELETE FROM sentence_word_relations WHERE sentence_id = :sentenceId"
        ).setParameter("sentenceId", sentenceId).executeUpdate()
        
        // イディオムとの関連を削除
        entityManager.createNativeQuery(
            "DELETE FROM sentence_idiom_relations WHERE sentence_id = :sentenceId"
        ).setParameter("sentenceId", sentenceId).executeUpdate()
        
        // 文法との関連を削除
        entityManager.createNativeQuery(
            "DELETE FROM sentence_grammar_relations WHERE sentence_id = :sentenceId"
        ).setParameter("sentenceId", sentenceId).executeUpdate()
    }
} 