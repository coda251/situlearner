package com.coda.situlearner.feature.word.detail.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.data.mapper.proficiencyType
import com.coda.situlearner.feature.word.detail.entry.navigation.WordDetailEntryRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant

internal class WordDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<WordDetailEntryRoute>()

    val uiState: StateFlow<WordDetailUiState> =
        wordRepository.getWordWithContexts(route.wordId).map {
            if (it == null) WordDetailUiState.Empty
            else WordDetailUiState.Success(
                wordWithContexts = it,
                wordProficiencyType = route.wordProficiencyType ?: it.word.proficiencyType,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = WordDetailUiState.Loading
        )

    fun setWordViewedDate(word: Word, date: Instant = Clock.System.now()) {
        viewModelScope.launch {
            wordRepository.setWordLastViewedDate(word, date)
        }
    }

    val quizStatsUiState: StateFlow<QuizStatsUiState> = flow {
        emit(
            QuizStatsUiState.Success(
                meaningQuizStats = wordRepository.getMeaningQuizStats(setOf(route.wordId))
                    .firstOrNull(),
                translationQuizStats = wordRepository.getTranslationQuizStats(route.wordId)
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = QuizStatsUiState.Loading
    )
}

internal sealed interface WordDetailUiState {
    data object Empty : WordDetailUiState
    data object Loading : WordDetailUiState
    data class Success(
        val wordWithContexts: WordWithContexts,
        val wordProficiencyType: WordProficiencyType,
    ) : WordDetailUiState
}

internal sealed interface QuizStatsUiState {
    data object Loading : QuizStatsUiState
    data class Success(
        val meaningQuizStats: MeaningQuizStats?,
        val translationQuizStats: TranslationQuizStats?,
    ) : QuizStatsUiState
}