package com.coda.situlearner.feature.home.settings.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.TranslationEvalBackend
import com.coda.situlearner.core.model.data.TranslationEvalPromptTemplate
import com.coda.situlearner.core.model.data.TranslationQuizPromptTemplate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsWordViewModel(
    private val preferenceRepository: UserPreferenceRepository,
    private val aiStateRepository: AiStateRepository
) : ViewModel() {

    val uiState = combine(
        preferenceRepository.userPreference,
        aiStateRepository.aiState,
    ) { preference, ai ->
        UiState.Success(
            wordLibraryLanguage = preference.wordLibraryLanguage,
            recommendedWordCount = preference.recommendedWordCount,
            quizWordCount = preference.quizWordCount,
            quizPromptTemplate = ai.quizPromptTemplate.data,
            evalPromptTemplate = ai.evalPromptTemplate.data,
            evalBackend = ai.evalBackend
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
    )

    fun setWordLibraryLanguage(language: Language) {
        viewModelScope.launch {
            preferenceRepository.setWordLibraryLanguage(language)
        }
    }

    fun setRecommendedWordCount(count: UInt) {
        viewModelScope.launch {
            preferenceRepository.setRecommendedWordCount(count)
        }
    }

    fun setQuizWordCount(count: UInt) {
        viewModelScope.launch {
            preferenceRepository.setQuizWordCount(count)
        }
    }

    fun setTranslationQuizPromptTemplate(template: String) {
        viewModelScope.launch {
            aiStateRepository.setTranslationQuizPromptTemplate(
                TranslationQuizPromptTemplate(template)
            )
        }
    }

    fun setTranslationEvalPromptTemplate(template: String) {
        viewModelScope.launch {
            aiStateRepository.setTranslationEvalPromptTemplate(
                TranslationEvalPromptTemplate(template)
            )
        }
    }

    fun setTranslationEvalBackend(evalBackend: TranslationEvalBackend) {
        viewModelScope.launch {
            aiStateRepository.setTranslationEvalBackend(evalBackend)
        }
    }
}

internal sealed interface UiState {
    data object Loading : UiState
    data class Success(
        val wordLibraryLanguage: Language,
        val recommendedWordCount: UInt,
        val quizWordCount: UInt,
        val quizPromptTemplate: String,
        val evalPromptTemplate: String,
        val evalBackend: TranslationEvalBackend
    ) : UiState
}