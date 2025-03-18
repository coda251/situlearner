package com.coda.situlearner.feature.word.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.feature.word.category.model.CategoryViewType
import com.coda.situlearner.feature.word.category.model.SortMode
import com.coda.situlearner.feature.word.category.model.WordSortBy
import com.coda.situlearner.feature.word.category.model.toMediaFileWithWords
import com.coda.situlearner.feature.word.category.navigation.WordCategoryRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class WordCategoryViewModel(
    savedStateHandle: SavedStateHandle,
    wordRepository: WordRepository
) : ViewModel() {

    val route = savedStateHandle.toRoute<WordCategoryRoute>()

    private val _wordOptionUiState = MutableStateFlow(
        WordOptionUiState.Success(
            sortMode = SortMode.Ascending,
            wordSortBy = WordSortBy.LastViewedDate
        )
    )
    val wordOptionUiState = _wordOptionUiState.asStateFlow()

    val uiState = combine(
        wordRepository.words,
        wordOptionUiState
    ) { words, options ->

        if (words.isEmpty()) WordCategoryUiState.Empty
        else {
            val selector = getWordSelector(options.sortMode, options.wordSortBy)

            when (route.categoryType) {
                WordCategoryType.All -> WordCategoryUiState.Success(
                    viewType = CategoryViewType.NoGroup,
                    wordSortBy = options.wordSortBy,
                    data = words.sortedBy(selector)
                )

                WordCategoryType.MediaCollection -> WordCategoryUiState.Success(
                    viewType = CategoryViewType.GroupByMediaFile,
                    wordSortBy = options.wordSortBy,
                    data = words.toMediaFileWithWords(
                        collectionId = route.categoryId,
                        wordSelector = selector
                    )
                )

                WordCategoryType.MediaFile -> WordCategoryUiState.Success(
                    viewType = CategoryViewType.NoGroup,
                    wordSortBy = options.wordSortBy,
                    data = words
                        .filter { word -> word.contexts.any { it.mediaFile?.id == route.categoryId } }
                        .sortedBy(selector)
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordCategoryUiState.Loading
    )

    private fun getWordSelector(
        sortMode: SortMode,
        wordSortBy: WordSortBy
    ): (WordWithContexts) -> Long {
        val sortModeFactor = when (sortMode) {
            SortMode.Ascending -> 1
            SortMode.Descending -> -1
        }

        return {
            when (wordSortBy) {
                WordSortBy.LastViewedDate -> (it.word.lastViewedDate?.toEpochMilliseconds()
                    ?: 0L) * sortModeFactor

                WordSortBy.Proficiency -> it.word.proficiency.level.toLong() * sortModeFactor
            }
        }
    }

    fun setWordSortMode(sortMode: SortMode) {
        _wordOptionUiState.value = _wordOptionUiState.value.copy(
            sortMode = sortMode
        )
    }

    fun setWordSortBy(wordSortBy: WordSortBy) {
        _wordOptionUiState.value = _wordOptionUiState.value.copy(
            wordSortBy = wordSortBy
        )
    }
}

internal sealed interface WordOptionUiState {
    data object Loading : WordOptionUiState
    data class Success(
        val sortMode: SortMode,
        val wordSortBy: WordSortBy
    ) : WordOptionUiState
}

internal sealed interface WordCategoryUiState {
    data object Loading : WordCategoryUiState
    data object Empty : WordCategoryUiState
    data class Success(
        val viewType: CategoryViewType,
        val wordSortBy: WordSortBy,
        val data: List<Any>, // Any for WordWithContexts or MediaFileWithWords
    ) : WordCategoryUiState
}