package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.cache.CoverImageCacheManager
import com.coda.situlearner.core.cache.SubtitleCacheManager
import com.coda.situlearner.core.data.mapper.asEntity
import com.coda.situlearner.core.data.mapper.asExternalModel
import com.coda.situlearner.core.data.mapper.asValue
import com.coda.situlearner.core.data.util.selectRecommendedWords
import com.coda.situlearner.core.database.dao.WordBankDao
import com.coda.situlearner.core.database.entity.MeaningQuizStatsEntity
import com.coda.situlearner.core.database.entity.WordWithContextsEntity
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordWithContexts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

internal class LocalWordRepository(
    private val wordBankDao: WordBankDao,
    private val subtitleCacheManager: SubtitleCacheManager,
    private val imageCacheManager: CoverImageCacheManager,
    preferenceRepository: UserPreferenceRepository,
) : WordRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val words = preferenceRepository.userPreference
        .map { it.wordLibraryLanguage }
        .distinctUntilChanged()
        .flatMapLatest { getWordWithContextsList(it) }

    override var cachedRecommendedWords: List<WordWithContexts> = emptyList()

    override fun getWordWithContextsList(language: Language): Flow<List<WordWithContexts>> {
        return wordBankDao.getWordWithContextsEntities(language.asValue())
            .map { wordWithContextsEntities ->
                wordWithContextsEntities
                    .map(WordWithContextsEntity::asExternalModel)
                    .map { it.resolveMediaUrl(subtitleCacheManager, imageCacheManager) }
            }
    }

    override fun getWordWithContext(
        mediaId: String,
        subtitleStartTimeInMs: Long,
        subtitleSourceText: String,
        wordStartIndex: Int,
        wordEndIndex: Int
    ): Flow<WordContext?> {
        return wordBankDao.getWordContextEntity(
            mediaId, subtitleStartTimeInMs, subtitleSourceText, wordStartIndex, wordEndIndex
        ).map {
            it?.asExternalModel()
        }
    }

    override suspend fun getWord(word: String, language: Language): Word? {
        return wordBankDao.getWordEntity(word, language.asValue())?.asExternalModel()
    }

    override suspend fun getWord(wordId: String): Word? {
        return wordBankDao.getWordEntity(wordId)?.asExternalModel()
    }

    override suspend fun getTranslationQuizWord(language: Language, currentDate: Instant): Word? {
        val candidates = wordBankDao.getTranslationQuizCandidates(
            language.asValue(),
            currentDate,
            proficiency = WordProficiency.Proficient.asValue()
        )
        if (candidates.isEmpty()) return null

        val statsEntityList =
            wordBankDao.getMeaningQuizStatsEntities(candidates.map { it.id }.toSet())
        val statsEntityMap = statsEntityList.associateBy { it.wordId }

        return candidates
            .mapNotNull { word ->
                statsEntityMap[word.id]?.let { stats -> word to stats.nextQuizDate }
            }
            .maxByOrNull { it.second }
            ?.first?.asExternalModel()
    }

    override fun getWordWithContexts(wordId: String): Flow<WordWithContexts?> {
        return wordBankDao.getWordWithContextEntity(wordId).map { wordWithContextsEntity ->
            wordWithContextsEntity?.asExternalModel()?.resolveMediaUrl(
                subtitleCacheManager, imageCacheManager
            )
        }
    }

    override suspend fun getWordWithContexts(
        language: Language,
        currentDate: Instant,
        count: UInt
    ): List<WordWithContexts> {
        return wordBankDao.getWordWithContextEntities(
            language.asValue(),
            currentDate,
            count.toInt()
        ).map { it.asExternalModel().resolveMediaUrl(subtitleCacheManager, imageCacheManager) }
    }

    override suspend fun insertWordWithContext(word: Word, wordContext: WordContext) {
        return wordBankDao.insertWordEntityAndWordContextEntity(
            word.asEntity(), wordContext.asEntity()
        )
    }

    override suspend fun deleteWordContext(wordContext: WordContext) {
        return wordBankDao.deleteWordContextEntity(wordContext.id)
    }

    override suspend fun setWordLastViewedDate(word: Word, date: Instant) {
        return wordBankDao.updateWordEntity(word.id, date)
    }

    override suspend fun getMeaningQuizStats(ids: Set<String>): List<MeaningQuizStats> {
        return wordBankDao.getMeaningQuizStatsEntities(ids)
            .map(MeaningQuizStatsEntity::asExternalModel)
    }

    override suspend fun upsertMeaningQuizStats(statsList: List<MeaningQuizStats>) {
        return wordBankDao.upsertMeaningQuizStatsEntity(statsList.map(MeaningQuizStats::asEntity))
    }

    override suspend fun updateWords(idToProficiency: Map<String, WordProficiency>) {
        return wordBankDao.updateWordEntities(idToProficiency.mapValues { it.value.asValue() })
    }

    override suspend fun getRecommendedWords(count: UInt): List<WordWithContexts> {
        cachedRecommendedWords =
            words.firstOrNull()?.let { selectRecommendedWords(it, count.toInt()) } ?: emptyList()
        return cachedRecommendedWords
    }

    override suspend fun deleteWord(word: Word) {
        return wordBankDao.deleteWordEntity(word.id)
    }

    override suspend fun updateWord(word: Word) {
        return wordBankDao.updateWordEntity(word.asEntity())
    }

    override suspend fun getTranslationQuizStats(wordId: String): TranslationQuizStats? {
        return wordBankDao.getTranslationQuizStatsEntity(wordId)?.asExternalModel()
    }

    override suspend fun upsertTranslationQuizStats(stats: TranslationQuizStats) {
        return wordBankDao.upsertTranslationQuizStatsEntity(stats.asEntity())
    }

    private fun WordWithContexts.resolveMediaUrl(
        subtitleCacheManager: SubtitleCacheManager,
        imageCacheManager: CoverImageCacheManager
    ) = this.copy(
        word = word,
        contexts = contexts.map {
            it.copy(
                wordContext = it.wordContext,
                mediaFile = subtitleCacheManager.run {
                    it.mediaFile?.resolveUrl()
                },
                mediaCollection = imageCacheManager.run {
                    it.mediaCollection?.resolveUrl()
                }
            )
        }
    )
}