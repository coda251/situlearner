package com.coda.situlearner.feature.home.word.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.feature.home.word.library.model.WordBook
import com.coda.situlearner.feature.home.word.library.model.toWordBooks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class WordLibraryViewModel(
    private val wordRepository: WordRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel() {

    val booksUiState: StateFlow<WordBooksUiState> = combine(
        wordRepository.words,
        userPreferenceRepository.userPreference
    ) { words, preference ->
        if (words.isEmpty()) WordBooksUiState.Empty(preference.wordLibraryLanguage)
        else WordBooksUiState.Success(words.toWordBooks())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordBooksUiState.Loading,
    )

    private val _wordsUiState =
        MutableStateFlow<RecommendedWordsUiState>(RecommendedWordsUiState.Loading)
    val wordsUiState = _wordsUiState.asStateFlow()

    init {
        getRecommendedWords()
    }

    private fun getRecommendedWords() {
        viewModelScope.launch {
            val preference = userPreferenceRepository.userPreference.first()
            val data = wordRepository.getRecommendedWords(preference.recommendedWordCount)
            if (data.isEmpty()) _wordsUiState.value = RecommendedWordsUiState.Empty
            else _wordsUiState.value = RecommendedWordsUiState.Success(
                wordContexts = data,
                offset = 0
            )
        }
    }

    fun setWordsOffset(offset: Int) {
        when (val state = _wordsUiState.value) {
            is RecommendedWordsUiState.Success -> {
                _wordsUiState.value = state.copy(offset = offset)
            }

            else -> {}
        }
    }

    fun setWordLibraryLanguage(language: Language) {
        viewModelScope.launch {
            userPreferenceRepository.setWordLibraryLanguage(language)
        }
    }
}

internal sealed interface WordBooksUiState {
    data object Loading : WordBooksUiState
    data class Empty(val language: Language) : WordBooksUiState
    data class Success(val books: List<WordBook>) : WordBooksUiState
}

internal sealed interface RecommendedWordsUiState {
    data object Loading : RecommendedWordsUiState
    data object Empty : RecommendedWordsUiState
    data class Success(
        val wordContexts: List<WordContextView>,
        val offset: Int,
    ) : RecommendedWordsUiState
}