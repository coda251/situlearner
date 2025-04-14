package com.coda.situlearner.feature.home.settings.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.feature.home.settings.common.model.VersionState
import com.coda.situlearner.feature.home.settings.common.util.getRelease
import com.coda.situlearner.feature.home.settings.common.util.toVersionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SettingsCommonViewModel(private val userPreferenceRepository: UserPreferenceRepository) :
    ViewModel() {

    val uiState = userPreferenceRepository.userPreference.map {
        SettingsCommonUiState.Success(
            darkThemeMode = it.darkThemeMode,
            themeColorMode = it.themeColorMode,
            wordLibraryLanguage = it.wordLibraryLanguage,
            quizWordCount = it.quizWordCount,
            recommendedWordCount = it.recommendedWordCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingsCommonUiState.Loading
    )

    private val _versionState = MutableStateFlow<VersionState>(VersionState.NotChecked)
    val versionState = _versionState.asStateFlow()

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

    fun setRecommendedWordCount(count: UInt) {
        viewModelScope.launch {
            userPreferenceRepository.setRecommendedWordCount(count)
        }
    }

    fun checkAppUpdate(currentVersion: String?) {
        viewModelScope.launch {
            _versionState.value = VersionState.Loading
            _versionState.value = withContext(Dispatchers.IO) {
                try {
                    getRelease().toVersionState(currentVersion)
                } catch (e: Exception) {
                    VersionState.Failed
                }
            }
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
        val recommendedWordCount: UInt,
    ) : SettingsCommonUiState
}