package com.coda.situlearner.feature.player.entry

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.infra.player.PlayerStateProvider
import com.coda.situlearner.infra.subkit.processor.Processor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

internal class PlayerSubtitleViewModel(processor: Processor) : ViewModel() {

    private val playerState = PlayerStateProvider.state

    @OptIn(ExperimentalCoroutinesApi::class)
    val subtitleUiState: StateFlow<SubtitleUiState> =
        playerState.flatMapLatest { it.playlist }
            .flatMapLatest { playlist ->
                flow {
                    emit(SubtitleUiState.Loading)

                    playlist.currentItem?.let { item ->
                        val subtitleFileContent = item.subtitleUrl?.let {
                            it.toUri().path?.let {
                                withContext(Dispatchers.IO) {
                                    processor.load(it)
                                }
                            }
                        }

                        if (subtitleFileContent == null || subtitleFileContent.subtitles.isEmpty()) {
                            emit(SubtitleUiState.Empty)
                        } else {
                            emit(
                                SubtitleUiState.Success(
                                    mediaId = item.id,
                                    language = subtitleFileContent.sourceLanguage,
                                    subtitles = subtitleFileContent.subtitles
                                )
                            )
                        }
                    } ?: run {
                        emit(SubtitleUiState.Empty)
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = SubtitleUiState.Loading
            )

}

internal sealed interface SubtitleUiState {
    data object Loading : SubtitleUiState
    data object Empty : SubtitleUiState
    data class Success(
        val mediaId: String,
        val language: Language,
        val subtitles: List<Subtitle>
    ) : SubtitleUiState
}