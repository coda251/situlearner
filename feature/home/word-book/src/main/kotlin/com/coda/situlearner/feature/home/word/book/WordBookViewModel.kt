package com.coda.situlearner.feature.home.word.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.feature.home.word.book.model.WordChapter
import com.coda.situlearner.feature.home.word.book.model.toChapters
import com.coda.situlearner.feature.home.word.book.navigation.WordBookRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class WordBookViewModel(
    savedStateHandle: SavedStateHandle,
    repository: WordRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<WordBookRoute>()

    val uiState = repository.words.map {
        val data = it.toChapters(route.id)
        if (data.isEmpty()) WordBookUiState.Empty
        else WordBookUiState.Success(data)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordBookUiState.Loading
    )
}

internal sealed interface WordBookUiState {

    data object Loading : WordBookUiState
    data object Empty : WordBookUiState
    data class Success(
        val chapters: List<WordChapter>
    ) : WordBookUiState
}
