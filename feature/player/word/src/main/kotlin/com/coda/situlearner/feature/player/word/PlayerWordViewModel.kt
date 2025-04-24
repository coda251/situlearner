package com.coda.situlearner.feature.player.word

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.feature.player.word.model.Translation
import com.coda.situlearner.feature.player.word.model.toTranslationFlow
import com.coda.situlearner.feature.player.word.navigation.PlayerWordRoute
import com.coda.situlearner.infra.subkit.translator.Translator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class PlayerWordViewModel(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    defaultTargetLanguage: Language = AppConfig.targetLanguage
) : ViewModel() {

    val route: PlayerWordRoute = savedStateHandle.toRoute()

    private val translators = Translator.getTranslators(
        sourceLanguage = route.language,
        targetLanguage = defaultTargetLanguage
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val wordContextUiState = wordRepository.getWordWithContext(
        mediaId = route.mediaId,
        subtitleStartTimeInMs = route.subtitleStartTimeInMs,
        subtitleSourceText = route.subtitleSourceText,
        wordStartIndex = route.wordStartIndex,
        wordEndIndex = route.wordEndIndex
    ).mapLatest { wordContext ->
        wordContext?.let { it ->
            val word = wordRepository.getWord(it.wordId)
            word?.let {
                WordContextUiState.Existed(
                    word = it,
                    wordContext = wordContext
                )
            } ?: WordContextUiState.Empty
        } ?: run {
            val word = wordRepository.getWord(route.word, route.language)
            word?.let {
                WordContextUiState.OnlyWord(it)
            } ?: WordContextUiState.Empty
        }
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordContextUiState.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val wordQueryUiState = wordContextUiState.flatMapLatest { state ->
        when (state) {
            WordContextUiState.Loading -> flowOf(WordQueryUiState.Loading)
            is WordContextUiState.OnlyWord -> flowOf(WordQueryUiState.ResultDb(state.word))
            is WordContextUiState.Existed -> flowOf(WordQueryUiState.ResultDb(state.word))
            WordContextUiState.Empty -> {
                if (translators.isEmpty()) flowOf(WordQueryUiState.NoTranslatorError)
                else combine(translators.map { it.toTranslationFlow(route.word) }) {
                    WordQueryUiState.ResultWeb(translations = it.toList())
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordQueryUiState.Loading
    )

    fun insertWordWithContext(wordInfo: WordInfo?) {
        viewModelScope.launch {
            val wordWithContext = createWordWithContext(wordInfo)
            wordRepository.insertWordWithContext(wordWithContext.first, wordWithContext.second)
        }
    }

    fun deleteWordContext(wordContext: WordContext) {
        viewModelScope.launch {
            wordRepository.deleteWordContext(wordContext)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun createWordWithContext(wordInfo: WordInfo?): Pair<Word, WordContext> {
        val wordId = Uuid.random().toString()
        val wordContextId = Uuid.random().toString()
        val word = Word(
            id = wordId,
            word = wordInfo?.word ?: route.word,
            language = route.language,
            dictionaryName = wordInfo?.dictionaryName,
            pronunciation = wordInfo?.pronunciation,
            meanings = wordInfo?.meanings ?: emptyList()
        )

        val wordContext = WordContext(
            id = wordContextId,
            wordId = wordId,
            mediaId = route.mediaId,
            subtitleStartTimeInMs = route.subtitleStartTimeInMs,
            subtitleEndTimeInMs = route.subtitleEndTimeInMs,
            subtitleSourceText = route.subtitleSourceText,
            subtitleTargetText = route.subtitleTargetText,
            wordStartIndex = route.wordStartIndex,
            wordEndIndex = route.wordEndIndex
        )

        return word to wordContext
    }
}

internal sealed interface WordContextUiState {
    data object Loading : WordContextUiState
    data object Empty : WordContextUiState
    data class OnlyWord(val word: Word) : WordContextUiState
    data class Existed(
        val word: Word,
        val wordContext: WordContext
    ) : WordContextUiState
}

internal sealed interface WordQueryUiState {
    data object Loading : WordQueryUiState
    data object NoTranslatorError : WordQueryUiState
    data class ResultDb(val word: Word) : WordQueryUiState
    data class ResultWeb(val translations: List<Translation>) : WordQueryUiState
}