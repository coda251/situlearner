package com.coda.situlearner.feature.home.settings.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.TranslationEvalBackend
import com.coda.situlearner.core.model.data.TranslationEvalPromptTemplate
import com.coda.situlearner.core.model.data.TranslationQuizPromptTemplate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsQuizViewModel(
    private val preferenceRepository: UserPreferenceRepository,
    private val aiStateRepository: AiStateRepository
) : ViewModel() {

    val uiState = combine(
        preferenceRepository.userPreference,
        aiStateRepository.aiState,
    ) { preference, ai ->
        UiState.Success(
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
        val quizWordCount: UInt,
        val quizPromptTemplate: String,
        val evalPromptTemplate: String,
        val evalBackend: TranslationEvalBackend
    ) : UiState
}