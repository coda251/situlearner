package com.coda.situlearner.feature.word.quiz.meaning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.feature.UserRating
import com.coda.situlearner.core.model.feature.mapper.toWordProficiency
import com.coda.situlearner.core.model.feature.mapper.updateWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock

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
            val words = wordRepository.getMeaningQuizWordWithContextsList(
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
                }.shuffled(),
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
                val infoInDb = wordRepository.getMeaningQuizStats(quizResult.keys)
                addAll(infoInDb)
                val keysInDb = infoInDb.map { it.wordId }.toSet()
                quizResult.keys.forEach {
                    if (it !in keysInDb) {
                        add(
                            MeaningQuizStats(
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
                it.updateWith(quizResult[it.wordId] ?: UserRating.Again)
            }
            wordRepository.upsertMeaningQuizStats(newQuizInfoList)

            // update proficiency
            wordRepository.updateWords(
                newQuizInfoList.associateBy(
                    keySelector = { it.wordId },
                    valueTransform = { it.toWordProficiency() }
                )
            )

            _uiState.value =
                WordQuizUiState.Complete(quizResult.values.groupingBy { it }.eachCount())
        }
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