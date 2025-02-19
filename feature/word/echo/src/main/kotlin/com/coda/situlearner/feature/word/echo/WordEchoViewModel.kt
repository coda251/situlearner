package com.coda.situlearner.feature.word.echo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.infra.player.PlayerStateProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class WordEchoViewModel(
    wordRepository: WordRepository
) : ViewModel() {

    val playerState = PlayerStateProvider.state

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = playerState
        .flatMapLatest { it.playlist }
        .map { playlist -> playlist.items.map { it.id } }
        .distinctUntilChanged()
        .flatMapLatest { ids ->
            wordRepository.getWordContexts(ids.toSet()).map { contexts ->
                val idToWordContext = contexts.associateBy { it.id }
                ids.mapNotNull { idToWordContext[it] }
            }
        }
        .map {
            if (it.isEmpty()) WordEchoUiState.Empty
            else WordEchoUiState.Success(wordContexts = it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = WordEchoUiState.Loading
        )
}

internal sealed interface WordEchoUiState {
    data object Loading : WordEchoUiState
    data object Empty : WordEchoUiState
    data class Success(val wordContexts: List<WordContext>) : WordEchoUiState
}