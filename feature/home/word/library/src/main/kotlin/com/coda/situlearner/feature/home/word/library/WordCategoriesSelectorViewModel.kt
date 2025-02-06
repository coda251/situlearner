package com.coda.situlearner.feature.home.word.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.cfg.LanguageConfig
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordCategoryType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class WordCategoriesSelectorViewModel(private val userPreferenceRepository: UserPreferenceRepository) :
    ViewModel() {

    val uiState: StateFlow<WordCategoriesSelectorUiState> =
        userPreferenceRepository.userPreference.map {
            WordCategoriesSelectorUiState.Success(
                languageChoices = LanguageConfig.sourceLanguages,
                filterLanguage = it.wordFilterLanguage,
                categoryType = it.wordCategoryType,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = WordCategoriesSelectorUiState.Loading
        )

    fun setWordFilterLanguage(language: Language) {
        viewModelScope.launch {
            userPreferenceRepository.setWordFilterLanguage(language)
        }
    }

    fun setWordCategoryType(wordCategoryType: WordCategoryType) {
        viewModelScope.launch {
            userPreferenceRepository.setWordCategoryType(wordCategoryType)
        }
    }
}

internal sealed interface WordCategoriesSelectorUiState {
    data object Loading : WordCategoriesSelectorUiState
    data class Success(
        val languageChoices: List<Language>,
        val filterLanguage: Language,
        val categoryType: WordCategoryType
    ) : WordCategoriesSelectorUiState
}