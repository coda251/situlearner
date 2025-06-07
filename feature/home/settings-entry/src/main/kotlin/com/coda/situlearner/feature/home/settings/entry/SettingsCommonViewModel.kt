package com.coda.situlearner.feature.home.settings.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.ChatbotConfig
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.feature.home.settings.entry.model.VersionState
import com.coda.situlearner.feature.home.settings.entry.util.getRelease
import com.coda.situlearner.feature.home.settings.entry.util.toVersionState
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SettingsCommonViewModel(
    private val userPreferenceRepository: UserPreferenceRepository,
    aiStateRepository: AiStateRepository,
    private val client: HttpClient,
) : ViewModel() {

    val uiState = combine(
        userPreferenceRepository.userPreference,
        aiStateRepository.aiState,
    ) { preference, ai ->
        SettingsCommonUiState.Success(
            darkThemeMode = preference.darkThemeMode,
            themeColorMode = preference.themeColorMode,
            wordLibraryLanguage = preference.wordLibraryLanguage,
            quizWordCount = preference.quizWordCount,
            recommendedWordCount = preference.recommendedWordCount,
            chatbotConfig = ai.configs.currentItem,
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
                    getRelease(client).toVersionState(currentVersion)
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
        val chatbotConfig: ChatbotConfig?,
    ) : SettingsCommonUiState
}