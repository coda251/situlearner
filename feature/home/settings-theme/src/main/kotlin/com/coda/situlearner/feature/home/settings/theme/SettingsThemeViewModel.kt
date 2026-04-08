package com.coda.situlearner.feature.home.settings.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.ThemeColorMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsThemeViewModel(
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel() {

    val uiState = userPreferenceRepository.userPreference.map {
        UiState.Success(
            darkThemeMode = it.darkThemeMode,
            themeColorMode = it.themeColorMode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
    )

    fun setDarkThemeMode(darkThemeMode: DarkThemeMode) {
        viewModelScope.launch {
            userPreferenceRepository.setDarkThemeMode(darkThemeMode)
        }
    }

    fun setThemeColorMode(themeColorMode: ThemeColorMode) {
        viewModelScope.launch {
            userPreferenceRepository.setThemeColorMode(themeColorMode)
        }
    }
}

internal sealed interface UiState {
    data object Loading : UiState
    data class Success(
        val darkThemeMode: DarkThemeMode,
        val themeColorMode: ThemeColorMode,
    ) : UiState
}