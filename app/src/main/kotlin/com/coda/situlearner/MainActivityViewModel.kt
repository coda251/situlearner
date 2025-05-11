package com.coda.situlearner

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.ThemeColorMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel(
    userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    val uiState = userPreferenceRepository.userPreference.map {
        MainActivityUiState.Success(
            darkThemeMode = it.darkThemeMode,
            themeColorMode = it.themeColorMode,
            themeColor = if (it.themeColorMode == ThemeColorMode.DynamicWithThumbnail)
                Color(it.thumbnailThemeColor) else Color(AppConfig.DEFAULT_THEME_COLOR)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MainActivityUiState.Loading,
    )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(
        val darkThemeMode: DarkThemeMode,
        val themeColorMode: ThemeColorMode,
        val themeColor: Color,
    ) : MainActivityUiState
}