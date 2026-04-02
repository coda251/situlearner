package com.coda.situlearner.feature.word.quiz.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.feature.word.quiz.entry.model.QuizState
import com.coda.situlearner.feature.word.quiz.entry.model.QuizTaskByDay
import com.coda.situlearner.feature.word.quiz.entry.model.asQuizState
import com.coda.situlearner.feature.word.quiz.entry.model.asTasks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.time.Clock

internal class EntryViewModel(
    private val preferenceRepository: UserPreferenceRepository,
    private val wordRepository: WordRepository,
    private val aiRepository: AiStateRepository,
) : ViewModel() {

    private val dayWindow = 14 // including today
    private val timeZone = TimeZone.currentSystemDefault()
    private val currentDate = Clock.System.now()
    private val dueDate = currentDate.plus(dayWindow - 1, DateTimeUnit.DAY, timeZone)

    val uiState = combine(
        getMeaningQuizFlow(),
        getTranslationQuizFlow(),
        hasChatBotFlow()
    ) { m, t, h ->
        UiState.Success(
            meaningQuizState = m.firstOrNull()?.nextQuizDate.asQuizState(currentDate),
            translationQuizState = t.firstOrNull()?.nextQuizDate.asQuizState(currentDate),
            tasks = (m to t).asTasks(currentDate, timeZone, dayWindow),
            hasChatbot = h
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getMeaningQuizFlow(): Flow<List<MeaningQuizStats>> =
        preferenceRepository.userPreference.map {
            it.wordLibraryLanguage
        }.flatMapLatest { language ->
            wordRepository.getMeaningQuizStats(language, dueDate)
                .map { statsList ->
                    statsList.sortedBy { it.nextQuizDate }
                }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getTranslationQuizFlow(): Flow<List<TranslationQuizStats>> =
        preferenceRepository.userPreference.map {
            it.wordLibraryLanguage
        }.flatMapLatest { language ->
            wordRepository.getTranslationQuizStats(language, dueDate)
                .map { statsList ->
                    statsList.sortedBy { it.nextQuizDate }
                }
        }

    private fun hasChatBotFlow(): Flow<Boolean> = aiRepository.aiState.map {
        it.configs.isNotEmpty()
    }
}

internal sealed interface UiState {
    data object Loading : UiState
    data class Success(
        val meaningQuizState: QuizState,
        val translationQuizState: QuizState,
        val tasks: List<QuizTaskByDay>,
        val hasChatbot: Boolean,
    ) : UiState
}