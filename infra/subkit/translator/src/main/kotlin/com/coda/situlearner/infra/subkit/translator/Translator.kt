package com.coda.situlearner.infra.subkit.translator

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.infra.subkit.translator.en.YouDaoEnglish
import com.coda.situlearner.infra.subkit.translator.ja.TioJapanese
import com.coda.situlearner.infra.subkit.translator.ja.YouDaoJapanese
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class Translator internal constructor(
    open val name: String,
    open val sourceLanguage: Language,
) {
    fun query(word: String): Flow<WordTranslationResult> = flow {
        emit(WordTranslationResult.Loading)
        try {
            val info = fetch(word)
            if (info.isEmpty()) emit(WordTranslationResult.Empty)
            else emit(WordTranslationResult.Success(info))
        } catch (e: Exception) {
            emit(WordTranslationResult.Error)
        }
    }.flowOn(Dispatchers.IO)

    internal abstract suspend fun fetch(word: String): List<WordInfo>

    companion object {
        fun getTranslators(sourceLanguage: Language, targetLanguage: Language): List<Translator> {
            if (targetLanguage != Language.Chinese) return emptyList()

            return when (sourceLanguage) {
                Language.English -> listOf(YouDaoEnglish())
                Language.Japanese -> listOf(YouDaoJapanese(), TioJapanese())
                else -> emptyList()
            }
        }
    }
}

sealed interface WordTranslationResult {
    data object Loading : WordTranslationResult
    data object Error : WordTranslationResult
    data object Empty : WordTranslationResult
    data class Success(
        val infos: List<WordInfo>
    ) : WordTranslationResult
}