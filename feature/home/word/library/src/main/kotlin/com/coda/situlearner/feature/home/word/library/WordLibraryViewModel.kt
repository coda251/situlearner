package com.coda.situlearner.feature.home.word.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.feature.home.word.library.model.WordBook
import com.coda.situlearner.feature.home.word.library.model.toWordBooks
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class WordLibraryViewModel(wordRepository: WordRepository) : ViewModel() {

    val uiState: StateFlow<WordLibraryUiState> = wordRepository.words.map { wordWithContextsList ->
        if (wordWithContextsList.isEmpty()) WordLibraryUiState.Empty
        else WordLibraryUiState.Success(
            wordWithContextsList.toWordBooks(),
            wordWithContextsList.sortedBy { it.word.lastViewedDate }
                .mapNotNull { it.contexts.firstOrNull() }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordLibraryUiState.Loading,
    )
}

internal sealed interface WordLibraryUiState {
    data object Loading : WordLibraryUiState
    data object Empty : WordLibraryUiState
    data class Success(
        val books: List<WordBook>,
        val wordContexts: List<WordContextView>
    ) : WordLibraryUiState
}