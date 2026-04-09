package com.coda.situlearner.feature.player.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.PlaybackOnWordClick
import com.coda.situlearner.core.model.data.SubtitleDisplayMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class PlayerEntryViewModel(
    preferenceRepository: UserPreferenceRepository
) : ViewModel() {
    val settingsUiState: StateFlow<PlayerSettingsUiState> =
        preferenceRepository.userPreference.map {
            PlayerSettingsUiState.Success(
                playbackOnWordClick = it.playbackOnWordClick,
                subtitleDisplayMode = it.subtitleDisplayMode
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PlayerSettingsUiState.Loading
        )
}

internal sealed interface PlayerSettingsUiState {
    data object Loading : PlayerSettingsUiState
    data class Success(
        val playbackOnWordClick: PlaybackOnWordClick,
        val subtitleDisplayMode: SubtitleDisplayMode
    ) : PlayerSettingsUiState
}