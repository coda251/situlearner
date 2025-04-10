package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordQuizInfo
import com.coda.situlearner.core.model.data.WordWithContexts
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface WordRepository {

    val words: Flow<List<WordWithContexts>>

    fun getWordWithContextsList(language: Language): Flow<List<WordWithContexts>>

    fun getWordContext(
        mediaId: String,
        subtitleStartTimeInMs: Long,
        subtitleSourceText: String,
        wordStartIndex: Int,
        wordEndIndex: Int,
    ): Flow<WordContext?>

    fun getWord(
        word: String,
        language: Language
    ): Flow<Word?>

    fun getWordWithContexts(wordId: String): Flow<WordWithContexts?>

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

    suspend fun getRecommendedWords(count: UInt): List<WordContextView>
}