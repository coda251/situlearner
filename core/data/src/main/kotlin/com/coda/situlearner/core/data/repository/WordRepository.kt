package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordProficiency
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

    /**
     * The selection order is that if a word:
     *  1. has not been selected for meaning quiz before
     *  2. has the lowest intervalDays if it has been selected
     */
    suspend fun getMeaningQuizWordWithContextsList(
        language: Language,
        currentDate: Instant,
        count: UInt
    ): List<WordWithContexts>

    suspend fun getMeaningQuizStats(ids: Set<String>): List<MeaningQuizStats>

    suspend fun upsertMeaningQuizStats(statsList: List<MeaningQuizStats>)

    suspend fun updateWords(idToProficiency: Map<String, WordProficiency>)

    suspend fun getRecommendedWords(count: UInt): List<WordWithContexts>

    suspend fun deleteWord(word: Word)

    /**
     * If the duplicate word is detected, then
     *  - word contexts belong to that word will now belong to the current word
     *  - the duplicate word will be removed
     */
    suspend fun updateWord(word: Word)

    /**
     * Only words with proficient meaning proficiency will be selected. The
     * selection order is that if a word:
     *  1. has not been selected for translation quiz before
     *  2. has the earliest nextQuizDate if it has been selected
     *  3. has the earliest createdDate
     */
    suspend fun getTranslationQuizWord(
        language: Language,
        currentDate: Instant
    ): Word?

    suspend fun getTranslationQuizStats(wordId: String): TranslationQuizStats?

    suspend fun upsertTranslationQuizStats(stats: TranslationQuizStats)

    suspend fun getMeaningQuizWordWithStats(
        language: Language,
        currentDate: Instant
    ): Pair<Word?, MeaningQuizStats?>
}