package com.coda.situlearner.feature.player.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.infra.subkit.translator.Translator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class PlayerWordViewModel(
    private val route: PlayerWordBottomSheetRoute,
    private val wordRepository: WordRepository,
    defaultTargetLanguage: Language = AppConfig.targetLanguage
) : ViewModel() {

    val wordContextUiState = wordRepository.getWordContext(
        mediaId = route.mediaId,
        subtitleStartTimeInMs = route.subtitleStartTimeInMs,
        subtitleSourceText = route.subtitleSourceText,
        wordStartIndex = route.wordStartIndex,
        wordEndIndex = route.wordEndIndex
    ).map {
        it?.let {
            WordContextUiState.Success(wordContext = it)
        } ?: WordContextUiState.Empty
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordContextUiState.Loading
    )

    val wordInfoUiState = wordRepository.getWord(route.word, route.language).map { word ->
        // first query local db
        word?.let {
            val dictionaryName = it.dictionaryName
            val meanings = it.meanings

            if (dictionaryName == null) null // go to query network
            else {
                if (meanings.isNullOrEmpty()) {
                    WordInfoUiState.Empty(
                        dictionaryName = dictionaryName,
                        pronunciation = it.pronunciation
                    )
                } else {
                    WordInfoUiState.Success(
                        dictionaryName = dictionaryName,
                        pronunciation = it.pronunciation,
                        meanings = meanings,
                    )
                }
            }
        } ?:
        // next query network
        run {
            withContext(Dispatchers.IO) {
                Translator.getTranslators(
                    route.language, defaultTargetLanguage
                ).getOrNull(0)?.let {
                    val wordInfo = it.query(route.word)
                    val meanings = wordInfo.meanings
                    if (meanings.isNullOrEmpty()) {
                        WordInfoUiState.Empty(
                            dictionaryName = wordInfo.dictionaryName,
                            pronunciation = wordInfo.pronunciation
                        )
                    } else {
                        WordInfoUiState.Success(
                            dictionaryName = wordInfo.dictionaryName,
                            pronunciation = wordInfo.pronunciation,
                            meanings = meanings,
                        )
                    }
                } ?: WordInfoUiState.Error
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordInfoUiState.Loading
    )

    @OptIn(ExperimentalUuidApi::class)
    fun insertWordWithContext(wordInfoUiState: WordInfoUiState) {
        viewModelScope.launch {
            val wordId = Uuid.random().toString()
            val wordContextId = Uuid.random().toString()
            val word = Word(
                id = wordId,
                word = route.word,
                language = route.language,
                dictionaryName = when (wordInfoUiState) {
                    WordInfoUiState.Loading, WordInfoUiState.Error -> null
                    is WordInfoUiState.Empty -> wordInfoUiState.dictionaryName
                    is WordInfoUiState.Success -> wordInfoUiState.dictionaryName
                },
                pronunciation = when (wordInfoUiState) {
                    WordInfoUiState.Loading, WordInfoUiState.Error -> null
                    is WordInfoUiState.Empty -> wordInfoUiState.pronunciation
                    is WordInfoUiState.Success -> wordInfoUiState.pronunciation
                },
                meanings = when (wordInfoUiState) {
                    WordInfoUiState.Loading, WordInfoUiState.Error, is WordInfoUiState.Empty -> null
                    is WordInfoUiState.Success -> wordInfoUiState.meanings
                }
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

            wordRepository.insertWordWithContext(word, wordContext)
        }
    }

    fun deleteWordContext(wordContext: WordContext) {
        viewModelScope.launch {
            wordRepository.deleteWordContext(wordContext)
        }
    }
}

internal data class PlayerWordBottomSheetRoute(
    val word: String,
    val language: Language,
    val mediaId: String,
    val subtitleIndex: Int,
    val subtitleStartTimeInMs: Long,
    val subtitleEndTimeInMs: Long,
    val subtitleSourceText: String,
    val subtitleTargetText: String?,
    val wordStartIndex: Int,
    val wordEndIndex: Int,
)

internal sealed interface WordContextUiState {
    data object Loading : WordContextUiState
    data object Empty : WordContextUiState // no such word context
    data class Success(val wordContext: WordContext) : WordContextUiState
}

internal sealed interface WordInfoUiState {
    data object Loading : WordInfoUiState
    data object Error : WordInfoUiState
    data class Empty(
        val dictionaryName: String,
        val pronunciation: String? = null
    ) : WordInfoUiState

    data class Success(
        val dictionaryName: String,
        val pronunciation: String? = null,
        val meanings: List<WordMeaning>,
    ) : WordInfoUiState
}