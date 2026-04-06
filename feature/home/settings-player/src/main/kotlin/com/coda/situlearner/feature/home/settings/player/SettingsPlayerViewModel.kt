package com.coda.situlearner.feature.home.settings.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.PlaybackOnWordClick
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsPlayerViewModel(
    private val preferenceRepository: UserPreferenceRepository
) : ViewModel() {

    val uiState: StateFlow<UiState> =
        preferenceRepository.userPreference.map {
            UiState.Success(
                playbackOnWordClick = it.playbackOnWordClick
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

    fun setPlaybackOnWordClick(playbackOnWordClick: PlaybackOnWordClick) {
        viewModelScope.launch {
            preferenceRepository.setPlaybackOnWordClick(playbackOnWordClick)
        }
    }
}

internal sealed interface UiState {
    data object Loading : UiState
    data class Success(
        val playbackOnWordClick: PlaybackOnWordClick
    ) : UiState
}