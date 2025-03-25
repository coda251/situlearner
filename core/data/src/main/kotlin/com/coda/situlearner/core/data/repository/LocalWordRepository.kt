package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.cache.CoverImageCacheManager
import com.coda.situlearner.core.cache.SubtitleCacheManager
import com.coda.situlearner.core.data.mapper.asEntity
import com.coda.situlearner.core.data.mapper.asExternalModel
import com.coda.situlearner.core.data.mapper.asValue
import com.coda.situlearner.core.data.util.selectRecommendedWords
import com.coda.situlearner.core.database.dao.WordBankDao
import com.coda.situlearner.core.database.entity.WordContextEntity
import com.coda.situlearner.core.database.entity.WordQuizInfoEntity
import com.coda.situlearner.core.database.entity.WordWithContextsEntity
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordQuizInfo
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

    override fun getWordWithContextsList(language: Language): Flow<List<WordWithContexts>> {
        return wordBankDao.getWordWithContextsEntities(language.asValue())
            .map { wordWithContextsEntities ->
                wordWithContextsEntities
                    .map(WordWithContextsEntity::asExternalModel)
                    .map { it.resolveMediaUrl(subtitleCacheManager, imageCacheManager) }
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

    override suspend fun setWordLastViewedDate(word: Word, date: Instant) {
        return wordBankDao.updateWordEntity(word.id, date)
    }

    override suspend fun getWordQuizInfo(ids: Set<String>): List<WordQuizInfo> {
        return wordBankDao.getWordQuizInfoEntities(ids).map(WordQuizInfoEntity::asExternalModel)
    }

    override suspend fun upsertWordQuizInfo(infoList: List<WordQuizInfo>) {
        return wordBankDao.upsertWordQuizInfoEntities(infoList.map(WordQuizInfo::asEntity))
    }

    override suspend fun updateWords(idToProficiency: Map<String, WordProficiency>) {
        return wordBankDao.updateWordEntities(idToProficiency.mapValues { it.value.asValue() })
    }

    override suspend fun getRecommendedWords(count: UInt): List<WordContextView> {
        return words.firstOrNull()?.let { selectRecommendedWords(it, count.toInt()) } ?: emptyList()
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