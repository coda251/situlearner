package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.PartOfSpeech
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.domain.WordCategoryList
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface WordRepository {

    val wordCategories: Flow<WordCategoryList>

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

    suspend fun setWordProficiency(word: Word, proficiency: WordProficiency)

    suspend fun insertWordWithContext(
        word: Word,
        wordContext: WordContext
    )

    suspend fun deleteWordContext(wordContext: WordContext)

    suspend fun setWordContextPOS(
        wordContext: WordContext,
        pos: PartOfSpeech
    )

    suspend fun setWordLastViewedDate(
        word: Word,
        date: Instant
    )
}