package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordQuizInfo
import com.coda.situlearner.core.model.data.WordWithContexts
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface WordRepository {

    val words: Flow<List<WordWithContexts>>

    /**
     * We assure that all the recommended words have one and only one wordContext (safe to use
     * List.single()). However, we can not assure that all the recommended words are the latest
     * ones (they could even have been deleted in the database).
     */
    val cachedRecommendedWords: List<WordWithContexts>

    fun getWordWithContextsList(language: Language): Flow<List<WordWithContexts>>

    fun getWordWithContext(
        mediaId: String,
        subtitleStartTimeInMs: Long,
        subtitleSourceText: String,
        wordStartIndex: Int,
        wordEndIndex: Int,
    ): Flow<WordContext?>

    suspend fun getWord(
        word: String,
        language: Language
    ): Word?

    suspend fun getWord(
        wordId: String
    ): Word?

    fun getWordWithContexts(wordId: String): Flow<WordWithContexts?>

    /**
     * This func will assure that no duplicate word (i.e. same word and same language) will
     * be inserted into the database.
     */
    suspend fun insertWordWithContext(
        word: Word,
        wordContext: WordContext
    )

    suspend fun deleteWordContext(wordContext: WordContext)

    suspend fun setWordLastViewedDate(
        word: Word,
        date: Instant
    )

    suspend fun getWordWithContexts(
        language: Language,
        currentDate: Instant,
        count: UInt
    ): List<WordWithContexts>

    suspend fun getWordQuizInfo(ids: Set<String>): List<WordQuizInfo>

    suspend fun upsertWordQuizInfo(infoList: List<WordQuizInfo>)

    suspend fun updateWords(idToProficiency: Map<String, WordProficiency>)

    suspend fun getRecommendedWords(count: UInt): List<WordWithContexts>

    suspend fun deleteWord(word: Word)

    suspend fun updateWord(word: Word)
}