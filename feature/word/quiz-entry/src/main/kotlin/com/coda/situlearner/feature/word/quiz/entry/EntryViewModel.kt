package com.coda.situlearner.feature.word.quiz.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.data.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

internal class EntryViewModel(
    private val preferenceRepository: UserPreferenceRepository,
    private val wordRepository: WordRepository,
    private val aiRepository: AiStateRepository,
) : ViewModel() {

    val uiState = combine(
        getMeaningQuizFlow(),
        getTranslationQuizFlow()
    ) { m, t ->
        UiState.Success(
            meaningQuizState = m,
            translationQuizState = t
        )
    }.stateIn(
        scope = viewModelScope,
        // NOTE: set stopTimeoutMillis to zero so the flow will emit (and update
        // Clock.System.now()) every time the screen is shown.
        started = SharingStarted.WhileSubscribed(),
        initialValue = UiState.Loading
    )

    private fun getMeaningQuizFlow(): Flow<MeaningQuizState> =
        preferenceRepository.userPreference.map {
            it.wordLibraryLanguage
        }.map {
            val (word, quizStats) = wordRepository.getMeaningQuizWordWithStats(
                it,
                Clock.System.now()
            )
            when {
                word != null -> MeaningQuizState.Success
                quizStats != null -> MeaningQuizState.WaitUntil(quizStats.nextQuizDate)
                else -> MeaningQuizState.NoWord
            }
        }

    private fun getTranslationQuizFlow() = combine(
        hasChatBotFlow(),
        hasTranslationWordFlow()
    ) { hasChatbot, hasWord ->
        when {
            !hasChatbot -> TranslationQuizState.NoChatbot
            !hasWord -> TranslationQuizState.NoWord
            else -> TranslationQuizState.Success
        }
    }

    private fun hasChatBotFlow(): Flow<Boolean> = aiRepository.aiState.map {
        it.configs.isNotEmpty()
    }

    private fun hasTranslationWordFlow(): Flow<Boolean> =
        preferenceRepository.userPreference.map {
            it.wordLibraryLanguage
        }.map {
            wordRepository.getTranslationQuizWord(
                it,
                Clock.System.now()
            ) != null
        }
}

internal sealed interface UiState {
    data object Loading : UiState
    data class Success(
        val meaningQuizState: MeaningQuizState,
        val translationQuizState: TranslationQuizState
    ) : UiState
}

internal sealed interface MeaningQuizState {
    data object Success : MeaningQuizState
    data class WaitUntil(val nextQuizDate: Instant) : MeaningQuizState
    data object NoWord : MeaningQuizState
}

internal enum class TranslationQuizState {
    NoChatbot,
    NoWord,
    Success
}