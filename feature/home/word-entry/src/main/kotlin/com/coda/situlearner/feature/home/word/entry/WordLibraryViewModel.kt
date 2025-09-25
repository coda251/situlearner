package com.coda.situlearner.feature.home.word.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.feature.home.word.entry.model.WordBook
import com.coda.situlearner.feature.home.word.entry.model.toWordBooks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
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
        else WordBooksUiState.Success(words.toWordBooks(preference.wordBookSortBy))
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

    /**
     * The recommended words will be refreshed when one of these scenarios takes place:
     *  - recommended word count changed
     *  - word library language changed
     *  - the size of words has changed and is still smaller than recommended word count
     */
    private fun getRecommendedWords() {
        viewModelScope.launch {
            userPreferenceRepository.userPreference
                .map { it.recommendedWordCount to it.wordLibraryLanguage }
                .distinctUntilChanged()
                .collectLatest { pair ->
                    val recommendedWordCount = pair.first
                    refreshWords(recommendedWordCount)
                    wordRepository.words
                        .map { it.size }
                        .distinctUntilChanged()
                        .filter { it < recommendedWordCount.toInt() }
                        // drop to avoid calling refreshWords twice when recommendedWordCount changed
                        // while size of words is still smaller than recommendedWordCount
                        .drop(1)
                        .collectLatest {
                            refreshWords(recommendedWordCount)
                        }
                }
        }
    }

    private suspend fun refreshWords(recommendedWordCount: UInt) {
        val data = wordRepository.getRecommendedWords(recommendedWordCount)
        _wordsUiState.value =
            if (data.isEmpty()) RecommendedWordsUiState.Empty
            else RecommendedWordsUiState.Success(
                wordContexts = data.map { it.contexts.single() },
                offset = 0
            )
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