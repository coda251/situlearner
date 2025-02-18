package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.cache.CoverImageCacheManager
import com.coda.situlearner.core.cache.SubtitleCacheManager
import com.coda.situlearner.core.cfg.LanguageConfig
import com.coda.situlearner.core.data.mapper.asEntity
import com.coda.situlearner.core.data.mapper.asExternalModel
import com.coda.situlearner.core.data.mapper.asValue
import com.coda.situlearner.core.database.dao.WordBankDao
import com.coda.situlearner.core.database.entity.WordContextEntity
import com.coda.situlearner.core.database.entity.WordWithContextsEntity
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.PartOfSpeech
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.data.mapper.resolveLanguage
import com.coda.situlearner.core.model.data.mapper.toWordCategoryList
import com.coda.situlearner.core.model.domain.TimeFrame
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

internal class LocalWordRepository(
    private val wordBankDao: WordBankDao,
    private val subtitleCacheManager: SubtitleCacheManager,
    private val imageCacheManager: CoverImageCacheManager,
    preferenceRepository: UserPreferenceRepository,
    defaultSourceLanguage: Language = LanguageConfig.sourceLanguages.first(),
    timeFrameProvider: (Instant?) -> TimeFrame = TimeFrame.defaultTimeFrameProvider
) : WordRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val wordCategories =
        preferenceRepository.userPreference.map { it.resolveLanguage(defaultSourceLanguage).wordFilterLanguage }
            .distinctUntilChanged()
            .flatMapLatest { getWordWithContextsList(it) }
            .combine(preferenceRepository.userPreference.map { it.wordCategoryType }) { data, categoryType ->
                data.toWordCategoryList(categoryType, timeFrameProvider)
            }

    override fun getWordWithContextsList(language: Language): Flow<List<WordWithContexts>> {
        return wordBankDao.getWordWithContextsEntities(language.asValue())
            .map { wordWithContextsEntities ->
                wordWithContextsEntities.map(WordWithContextsEntity::asExternalModel)
                    .map { wordWithContexts ->
                        wordWithContexts.copy(
                            word = wordWithContexts.word,
                            contexts = wordWithContexts.contexts.map {
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
            }
    }

    override fun getWordContext(
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

    override fun getWord(word: String, language: Language): Flow<Word?> {
        return wordBankDao.getWordEntity(word, language.asValue()).map {
            it?.asExternalModel()
        }
    }

    override fun getWordWithContexts(wordId: String): Flow<WordWithContexts?> {
        return wordBankDao.getWordWithContextEntity(wordId).map { wordWithContextsEntity ->
            wordWithContextsEntity?.asExternalModel()?.let { wordWithContexts ->
                wordWithContexts.copy(
                    word = wordWithContexts.word,
                    contexts = wordWithContexts.contexts.map {
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
        }
    }

    override fun getWordContexts(ids: Set<String>): Flow<List<WordContext>> {
        return wordBankDao.getWordContextEntities(ids)
            .map { it.map(WordContextEntity::asExternalModel) }
    }

    override suspend fun setWordProficiency(word: Word, proficiency: WordProficiency) {
        return wordBankDao.updateWordEntity(word.id, proficiency.asValue())
    }

    override suspend fun insertWordWithContext(word: Word, wordContext: WordContext) {
        return wordBankDao.insertWordEntityAndWordContextEntity(
            word.asEntity(), wordContext.asEntity()
        )
    }

    override suspend fun deleteWordContext(wordContext: WordContext) {
        return wordBankDao.deleteWordContextEntity(wordContext.id)
    }

    override suspend fun setWordContextPOS(wordContext: WordContext, pos: PartOfSpeech) {
        return wordBankDao.updateWordContextEntity(wordContext.id, pos.asValue())
    }

    override suspend fun setWordLastViewedDate(word: Word, date: Instant) {
        return wordBankDao.updateWordEntity(word.id, date)
    }
}