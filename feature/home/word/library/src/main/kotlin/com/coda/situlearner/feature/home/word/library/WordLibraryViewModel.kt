package com.coda.situlearner.feature.home.word.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.domain.WordCategoryList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class WordLibraryViewModel(wordRepository: WordRepository) : ViewModel() {

    val uiState: StateFlow<WordLibraryUiState> = wordRepository.wordCategories.map {
        if (it.isEmpty()) WordLibraryUiState.Empty
        else WordLibraryUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordLibraryUiState.Loading,
    )
}

internal sealed interface WordLibraryUiState {
    data object Loading : WordLibraryUiState
    data object Empty : WordLibraryUiState
    data class Success(val categories: WordCategoryList) : WordLibraryUiState
}