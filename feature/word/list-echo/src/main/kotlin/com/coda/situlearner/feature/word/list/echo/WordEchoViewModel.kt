package com.coda.situlearner.feature.word.list.echo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Word
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
            wordRepository.words.map { wordWithContextsList ->
                val contextIdToPair = wordWithContextsList.flatMap { wordWithContexts ->
                    wordWithContexts.contexts.map { context ->
                        wordWithContexts.word to context.wordContext
                    }
                }.associateBy { it.second.id }
                ids.mapNotNull { contextIdToPair[it] }
            }
        }
        .map {
            if (it.isEmpty()) WordEchoUiState.Empty
            else WordEchoUiState.Success(it)
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
    data class Success(val words: List<Pair<Word, WordContext>>) : WordEchoUiState
}