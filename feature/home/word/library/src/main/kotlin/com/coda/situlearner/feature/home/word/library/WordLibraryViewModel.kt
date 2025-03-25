package com.coda.situlearner.feature.home.word.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.feature.home.word.library.model.WordBook
import com.coda.situlearner.feature.home.word.library.model.toWordBooks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class WordLibraryViewModel(
    private val wordRepository: WordRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel() {

    val booksUiState: StateFlow<WordBooksUiState> =
        wordRepository.words.map { wordWithContextsList ->
            if (wordWithContextsList.isEmpty()) WordBooksUiState.Empty
            else WordBooksUiState.Success(wordWithContextsList.toWordBooks())
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
            _wordsUiState.value = RecommendedWordsUiState.Success(
                wordContexts = wordRepository.getRecommendedWords(preference.recommendedWordCount),
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
}

internal sealed interface WordBooksUiState {
    data object Loading : WordBooksUiState
    data object Empty : WordBooksUiState
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