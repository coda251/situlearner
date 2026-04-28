package com.coda.situlearner.feature.player.fullscreen

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.PlaybackOnWordClick
import com.coda.situlearner.core.model.data.SubtitleDisplayMode
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.infra.player.PlayerStateProvider
import com.coda.situlearner.infra.subkit.processor.Processor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

internal class PlayerViewModel(
    processor: Processor,
    preferenceRepository: UserPreferenceRepository
) : ViewModel() {

    // NOTE: basically the same as in
    // PlayerSubtitleViewModel and PlayerEntryViewModel in
    // feature/player/entry,
    // we may refactor this in the future

    private val playerState = PlayerStateProvider.state

    @OptIn(ExperimentalCoroutinesApi::class)
    private val subtitleFlow: Flow<ParsedSubtitleData?> = playerState
        .flatMapLatest { it.playlist }
        .map { it.currentItem }
        .distinctUntilChanged()
        .mapLatest { item ->
            if (item == null) return@mapLatest null
            val path = item.subtitleUrl?.toUri()?.path ?: return@mapLatest null
            val content = withContext(Dispatchers.IO) { processor.load(path) }

            if (content == null || content.subtitles.isEmpty()) {
                null
            } else {
                ParsedSubtitleData(
                    mediaId = item.id,
                    language = content.sourceLanguage,
                    subtitles = content.subtitles
                )
            }
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    val subtitleUiState: StateFlow<SubtitleUiState> = combine(
        subtitleFlow,
        playerState.flatMapLatest { it.positionInMs }
    ) { parsedData, positionInMs ->
        if (parsedData == null) {
            return@combine SubtitleUiState.Empty
        }

        val subtitles = parsedData.subtitles
        val result = subtitles.binarySearch {
            compareValues(it.startTimeInMs, positionInMs)
        }
        val index = if (result >= 0) result else -(result + 2)

        val prev = subtitles.getOrNull(index - 1)
        val current = subtitles.getOrNull(index)
        val next = subtitles.getOrNull(index + 1)

        SubtitleUiState.Success(
            mediaId = parsedData.mediaId,
            language = parsedData.language,
            prevSubtitle = prev,
            currentSubtitle = current,
            nextSubtitle = next
        )
    }
        .onStart { emit(SubtitleUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SubtitleUiState.Loading
        )


    val settingsUiState: StateFlow<SettingsUiState> =
        preferenceRepository.userPreference.map {
            SettingsUiState.Success(
                playbackOnWordClick = it.playbackOnWordClick,
                subtitleDisplayMode = it.subtitleDisplayMode,
                darkThemeMode = it.darkThemeMode
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SettingsUiState.Loading
        )
}

internal sealed interface SubtitleUiState {
    data object Loading : SubtitleUiState
    data object Empty : SubtitleUiState
    data class Success(
        val mediaId: String,
        val language: Language,
        val prevSubtitle: Subtitle?,
        val currentSubtitle: Subtitle?,
        val nextSubtitle: Subtitle?
    ) : SubtitleUiState
}

internal sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val playbackOnWordClick: PlaybackOnWordClick,
        val subtitleDisplayMode: SubtitleDisplayMode,
        val darkThemeMode: DarkThemeMode,
    ) : SettingsUiState
}

private data class ParsedSubtitleData(
    val mediaId: String,
    val language: Language,
    val subtitles: List<Subtitle>
)