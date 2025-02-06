package com.coda.situlearner.feature.word.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.PartOfSpeech
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.domain.TimeFrame
import com.coda.situlearner.core.model.domain.WordCategory
import com.coda.situlearner.core.model.domain.WordCategoryList
import com.coda.situlearner.core.model.domain.WordMediaCategory
import com.coda.situlearner.core.model.domain.WordPOSCategory
import com.coda.situlearner.core.model.domain.WordProficiencyCategory
import com.coda.situlearner.core.model.domain.WordViewedDateCategory
import com.coda.situlearner.feature.word.category.navigation.WordCategoryRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class WordCategoryViewModel(
    savedStateHandle: SavedStateHandle,
    wordRepository: WordRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<WordCategoryRoute>()

    val uiState = wordRepository.wordCategories.map { it ->
        when (it.categoryType) {
            WordCategoryType.LastViewedDate -> {
                it.toWords<WordViewedDateCategory> { it.timeFrame == TimeFrame.valueOf(route.categoryId) }
            }

            WordCategoryType.Proficiency -> {
                it.toWords<WordProficiencyCategory> {
                    it.proficiency == WordProficiency.valueOf(
                        route.categoryId
                    )
                }
            }

            WordCategoryType.PartOfSpeech -> {
                it.toWords<WordPOSCategory> { it.partOfSpeech == PartOfSpeech.valueOf(route.categoryId) }
            }

            WordCategoryType.Media -> {
                it.toWords<WordMediaCategory> { it.collection.id == route.categoryId }
            }
        }
    }.map {
        if (it.isNullOrEmpty()) WordCategoryUiState.Empty
        else WordCategoryUiState.Success(words = it)
    }.catch {
        if (it is IllegalArgumentException) {
            emit(WordCategoryUiState.Error)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordCategoryUiState.Loading
    )

    private inline fun <reified T : WordCategory> WordCategoryList.toWords(
        filter: (T) -> Boolean
    ) = this.asTypedCategoryList<T>().firstOrNull(filter)?.let { category ->
        category.wordWithContextsList.map { it.word }
    }
}

internal sealed interface WordCategoryUiState {
    data object Error : WordCategoryUiState
    data object Loading : WordCategoryUiState
    data object Empty : WordCategoryUiState
    data class Success(val words: List<Word>) : WordCategoryUiState
}