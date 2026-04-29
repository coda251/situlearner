package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.cache.CoverImageCacheManager
import com.coda.situlearner.core.cache.SubtitleCacheManager
import com.coda.situlearner.core.data.mapper.asEntity
import com.coda.situlearner.core.data.mapper.asExternalModel
import com.coda.situlearner.core.data.mapper.asValue
import com.coda.situlearner.core.data.util.selectRecommendedWords
import com.coda.situlearner.core.database.dao.WordBankDao
import com.coda.situlearner.core.database.entity.MeaningQuizStatsEntity
import com.coda.situlearner.core.database.entity.MediaCollectionEntity
import com.coda.situlearner.core.database.entity.MediaFileEntity
import com.coda.situlearner.core.database.entity.WordEntity
import com.coda.situlearner.core.database.entity.WordWithContextsEntity
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.feature.mapper.toWordProficiency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

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
            .map {
                it.asExternalModelsWithResolvedUrls(
                    subtitleCacheManager,
                    imageCacheManager
                )
            }
            .flowOn(Dispatchers.Default)
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
        return wordBankDao.getWordEntity(
            language.asValue(),
            currentDate
        )?.asExternalModel()
    }

    override fun getWordWithContexts(wordId: String): Flow<WordWithContexts?> {
        return wordBankDao.getWordWithContextEntity(wordId).map { wordWithContextsEntity ->
            wordWithContextsEntity?.asExternalModel()?.resolveMediaUrl(
                subtitleCacheManager, imageCacheManager
            )
        }
    }

    override suspend fun getMeaningQuizWordWithContextsList(
        language: Language,
        currentDate: Instant,
        count: UInt
    ): List<WordWithContexts> {
        return wordBankDao.getWordWithContextEntities(
            language.asValue(),
            currentDate,
            count.toInt()
        ).asExternalModelsWithResolvedUrls(subtitleCacheManager, imageCacheManager)
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

    override suspend fun updateMeaningQuizStats(statsList: List<MeaningQuizStats>) {
        val statsWithProficiency = statsList.map {
            Pair(
                it.asEntity(),
                it.toWordProficiency().asValue()
            )
        }
        return wordBankDao.updateMeaningQuizStats(statsWithProficiency)
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

    override suspend fun updateTranslationQuizStats(stats: TranslationQuizStats) {
        return wordBankDao.updateTranslationQuizStats(
            stats.asEntity(),
            stats.toWordProficiency().asValue()
        )
    }

    override fun getMeaningQuizStats(
        language: Language,
        due: Instant
    ): Flow<List<MeaningQuizStats>> = combine(
        wordBankDao.getWordEntities(language.asValue()),
        wordBankDao.getMeaningQuizStatsEntities(language.asValue(), due)
    ) { allWords, quizzedEntities ->
        val neverQuizzed = allWords
            .filter { it.meaningProficiency.asExternalModel() == WordProficiency.Unset }
            .map {
                MeaningQuizStats.create(
                    wordId = it.id,
                    currentDate = Instant.DISTANT_PAST
                )
            }
        val quizzed = quizzedEntities.map { it.asExternalModel() }
        neverQuizzed + quizzed
    }

    override fun getTranslationQuizStats(
        language: Language,
        due: Instant
    ): Flow<List<TranslationQuizStats>> = combine(
        wordBankDao.getWordEntities(language.asValue()),
        wordBankDao.getTranslationQuizStatsEntities(language.asValue(), due)
    ) { allWords, quizzedEntities ->
        val neverQuizzed = allWords
            .filter {
                val m = it.meaningProficiency
                val t = it.translationProficiency
                m.asExternalModel() == WordProficiency.Proficient &&
                        (t == null || t.asExternalModel() == WordProficiency.Unset)
            }
            .map {
                TranslationQuizStats.create(
                    wordId = it.id,
                    currentDate = Instant.DISTANT_PAST
                )
            }
        val quizzed = quizzedEntities.map { it.asExternalModel() }
        neverQuizzed + quizzed
    }

    override fun getWords(language: Language): Flow<List<Word>> {
        return wordBankDao.getWordEntities(language.asValue()).map {
            it.map(WordEntity::asExternalModel)
        }
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

    private fun List<WordWithContextsEntity>.asExternalModelsWithResolvedUrls(
        subtitleCacheManager: SubtitleCacheManager,
        imageCacheManager: CoverImageCacheManager,
    ): List<WordWithContexts> {
        // Phase 1: collect unique entities
        val uniqueFileEntities = mutableMapOf<String, MediaFileEntity>()
        val uniqueCollectionEntities = mutableMapOf<String, MediaCollectionEntity>()
        this.flatMap { it.contexts }.forEach { ctxView ->
            ctxView.mediaFile?.let { uniqueFileEntities.putIfAbsent(it.id, it) }
            ctxView.mediaCollection?.let { uniqueCollectionEntities.putIfAbsent(it.id, it) }
        }

        // Phase 2: resolve each unique entity once
        val resolvedFiles = uniqueFileEntities.mapValues { (_, fileEntity) ->
            val domain = fileEntity.asExternalModel()
            subtitleCacheManager.run { domain.resolveUrl() }
        }
        val resolvedCollections = uniqueCollectionEntities.mapValues { (_, colEntity) ->
            val domain = colEntity.asExternalModel()
            imageCacheManager.run { domain.resolveUrl() }
        }

        // Phase 3: apply resolved entities back
        return this.map { entity ->
            WordWithContexts(
                word = entity.word.asExternalModel(),
                contexts = entity.contexts.map { ctxView ->
                    WordContextView(
                        wordContext = ctxView.wordContext.asExternalModel(),
                        mediaFile = ctxView.mediaFile?.let { resolvedFiles[it.id] },
                        mediaCollection = ctxView.mediaCollection?.let { resolvedCollections[it.id] },
                    )
                }
            )
        }
    }
}