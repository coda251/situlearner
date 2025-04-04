package com.coda.situlearner.feature.word.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordQuizInfo
import com.coda.situlearner.feature.word.quiz.model.UserRating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

internal class WordQuizViewModel(
    private val wordRepository: WordRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WordQuizUiState>(WordQuizUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val quizResult = mutableMapOf<String, UserRating>()

    init {
        getWords()
    }

    private fun getWords() {
        viewModelScope.launch {
            val preference = userPreferenceRepository.userPreference.first()
            val words = wordRepository.getWordWithContexts(
                language = preference.wordLibraryLanguage,
                currentDate = Clock.System.now(),
                count = preference.quizWordCount,
            )

            if (words.isEmpty()) _uiState.value = WordQuizUiState.Empty
            else _uiState.value = WordQuizUiState.Success(
                words = words.map { wordWithContexts ->
                    wordWithContexts.word to wordWithContexts.contexts.run {
                        if (isEmpty()) null
                        else filter { it.mediaFile != null }.randomOrNull() ?: random()
                    }
                },
                currentIndex = 0
            )
        }
    }

    fun onRate(word: Word, rating: UserRating) {
        quizResult[word.id] = rating
    }

    fun onNext(currentIndex: Int) {
        when (val state = _uiState.value) {
            is WordQuizUiState.Success -> {
                val nextIndex = currentIndex + 1
                if (nextIndex !in state.words.indices) {
                    handelQuizResult()
                } else {
                    _uiState.value = state.copy(currentIndex = nextIndex)
                }
            }

            else -> {}
        }
    }

    private fun handelQuizResult() {
        _uiState.value = WordQuizUiState.Summarizing
        viewModelScope.launch {
            val quizInfoList = buildList {
                val infoInDb = wordRepository.getWordQuizInfo(quizResult.keys)
                addAll(infoInDb)
                val keysInDb = infoInDb.map { it.wordId }.toSet()
                quizResult.keys.forEach {
                    if (it !in keysInDb) {
                        add(
                            WordQuizInfo(
                                wordId = it,
                                easeFactor = 2.5,
                                intervalDays = 1,
                                nextQuizDate = Clock.System.now()
                            )
                        )
                    }
                }
            }

            // update word quiz info
            val newQuizInfoList = quizInfoList.map {
                updateQuizInfo(it, quizResult[it.wordId] ?: UserRating.Again)
            }
            wordRepository.upsertWordQuizInfo(newQuizInfoList)

            // update proficiency
            wordRepository.updateWords(
                newQuizInfoList.associateBy(
                    keySelector = { it.wordId },
                    valueTransform = { calcProficiency(it.intervalDays) }
                )
            )

            _uiState.value =
                WordQuizUiState.Complete(quizResult.values.groupingBy { it }.eachCount())
        }
    }

    private fun updateQuizInfo(
        old: WordQuizInfo,
        rating: UserRating
    ): WordQuizInfo {
        // refer to sm-2
        val oldEaseFactor = old.easeFactor
        val oldInterval = old.intervalDays

        val newEaseFactor: Double
        val newInterval: Int

        when (rating) {
            UserRating.Again -> {
                newInterval = 1
                newEaseFactor = (oldEaseFactor - 0.2).coerceAtLeast(1.3)
            }

            UserRating.Hard -> {
                newInterval = (oldInterval * 1.2).toInt().coerceAtLeast(1)
                newEaseFactor = (oldEaseFactor - 0.15).coerceAtLeast(1.3)
            }

            UserRating.Good -> {
                newInterval = (oldInterval * oldEaseFactor).toInt().coerceAtLeast(1)
                newEaseFactor = oldEaseFactor
            }

            UserRating.Easy -> {
                newInterval = (oldInterval * oldEaseFactor * 1.3).toInt().coerceAtLeast(1)
                newEaseFactor = oldEaseFactor + 0.15
            }
        }

        return WordQuizInfo(
            wordId = old.wordId,
            easeFactor = newEaseFactor,
            intervalDays = newInterval,
            nextQuizDate = Clock.System.now()
                .plus(newInterval.toLong().toDuration(DurationUnit.DAYS))
        )
    }

    private fun calcProficiency(intervalDays: Int) = when {
        intervalDays == 0 -> WordProficiency.Unset
        intervalDays <= 2 -> WordProficiency.Beginner
        intervalDays <= 7 -> WordProficiency.Intermediate
        else -> WordProficiency.Proficient
    }
}

internal sealed interface WordQuizUiState {
    data object Loading : WordQuizUiState
    data object Empty : WordQuizUiState
    data class Success(
        val words: List<Pair<Word, WordContextView?>>,
        val currentIndex: Int,
    ) : WordQuizUiState

    data object Summarizing : WordQuizUiState
    data class Complete(
        val result: Map<UserRating, Int>
    ) : WordQuizUiState
}