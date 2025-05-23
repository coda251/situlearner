package com.coda.situlearner.feature.word.detail.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.feature.word.detail.entry.navigation.WordDetailEntryRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

internal class WordDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<WordDetailEntryRoute>()

    val uiState: StateFlow<WordDetailUiState> =
        wordRepository.getWordWithContexts(route.wordId).map {
            if (it == null) WordDetailUiState.Empty
            else WordDetailUiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = WordDetailUiState.Loading
        )

    fun setWordViewedDate(word: Word, date: Instant = Clock.System.now()) {
        viewModelScope.launch {
            wordRepository.setWordLastViewedDate(word, date)
        }
    }
}

internal sealed interface WordDetailUiState {
    data object Empty : WordDetailUiState
    data object Loading : WordDetailUiState
    data class Success(val wordWithContexts: WordWithContexts) : WordDetailUiState
}