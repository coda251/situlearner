package com.coda.situlearner.feature.word.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.infra.WordTranslation
import com.coda.situlearner.feature.word.edit.navigation.WordEditRoute
import com.coda.situlearner.infra.subkit.translator.Translator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class WordEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val defaultTargetLanguage: Language = AppConfig.targetLanguage
) : ViewModel() {

    private val route = savedStateHandle.toRoute<WordEditRoute>()

    private val _editUiState = MutableStateFlow<WordEditUiState>(WordEditUiState.Loading)
    val editUiState = _editUiState.asStateFlow()

    private val queryWord = MutableStateFlow<Pair<String, Language>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val queryUiState = queryWord
        .filterNotNull()
        .distinctUntilChangedBy { it.second.name + it.first.lowercase() }
        .flatMapLatest { wordToLanguage ->
            val translators = Translator.getTranslators(
                sourceLanguage = wordToLanguage.second,
                targetLanguage = defaultTargetLanguage
            )

            if (translators.isEmpty()) flowOf(WordQueryUiState.NoTranslatorError)
            else combine(translators.map { it.query(wordToLanguage.first) }) {
                WordQueryUiState.Result(
                    queryWord = wordToLanguage.first,
                    translations = it.toList()
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = WordQueryUiState.Idle
        )

    init {
        getWord()
    }

    private fun getWord() {
        viewModelScope.launch {
            _editUiState.value = WordEditUiState.Loading
            _editUiState.value = withContext(Dispatchers.IO) {
                wordRepository.getWord(route.wordId)?.let {
                    WordEditUiState.Success(it)
                } ?: WordEditUiState.Empty
            }
        }
    }

    fun updateWord(word: Word) {
        viewModelScope.launch {
            _editUiState.value = WordEditUiState.Saving
            wordRepository.updateWord(word)
            _editUiState.value = WordEditUiState.Saved
        }
    }

    fun getTranslation(word: String, language: Language) {
        queryWord.value = word to language
    }
}

internal sealed interface WordEditUiState {
    data object Loading : WordEditUiState
    data object Empty : WordEditUiState
    data class Success(val word: Word) : WordEditUiState
    data object Saving : WordEditUiState
    data object Saved : WordEditUiState
}

internal sealed interface WordQueryUiState {
    data object Idle : WordQueryUiState
    data object NoTranslatorError : WordQueryUiState
    data class Result(
        val queryWord: String,
        val translations: List<WordTranslation>
    ) : WordQueryUiState
}