package com.coda.situlearner.feature.home.settings.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.ThemeColorMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsCommonViewModel(private val userPreferenceRepository: UserPreferenceRepository) :
    ViewModel() {

    val uiState = userPreferenceRepository.userPreference.map {
        SettingsCommonUiState.Success(
            darkThemeMode = it.darkThemeMode,
            themeColorMode = it.themeColorMode,
            wordLibraryLanguage = it.wordLibraryLanguage,
            quizWordCount = it.quizWordCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingsCommonUiState.Loading
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

    fun setWordLibraryLanguage(language: Language) {
        viewModelScope.launch {
            userPreferenceRepository.setWordLibraryLanguage(language)
        }
    }

    fun setQuizWordCount(count: UInt) {
        viewModelScope.launch {
            userPreferenceRepository.setQuizWordCount(count)
        }
    }
}

internal sealed interface SettingsCommonUiState {
    data object Loading : SettingsCommonUiState
    data class Success(
        val darkThemeMode: DarkThemeMode,
        val themeColorMode: ThemeColorMode,
        val wordLibraryLanguage: Language,
        val quizWordCount: UInt,
    ) : SettingsCommonUiState
}