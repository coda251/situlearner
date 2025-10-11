package com.coda.situlearner.feature.word.list.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.data.mapper.proficiencyType
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.word.list.entry.model.SortMode
import com.coda.situlearner.feature.word.list.entry.model.WordSortBy
import com.coda.situlearner.feature.word.list.entry.navigation.WordListEntryRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class WordListViewModel(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<WordListEntryRoute>()

    private val _wordOptionUiState = MutableStateFlow(
        WordOptionUiState.Success(
            sortMode = SortMode.Ascending,
            wordSortBy = WordSortBy.Proficiency
        )
    )
    val wordOptionUiState = _wordOptionUiState.asStateFlow()

    val uiState = combine(
        wordRepository.words,
        wordOptionUiState
    ) { words, options ->
        if (words.isEmpty()) WordListUiState.Empty
        else {

            val data = when (route.wordListType) {
                WordListType.All -> words
                WordListType.MediaCollection -> words
                    .filter { word ->
                        word.contexts.any { it.mediaCollection?.id == route.id }
                    }

                WordListType.MediaFile -> words
                    .filter { word -> word.contexts.any { it.mediaFile?.id == route.id } }

                WordListType.NoMedia -> words
                    .filter { word -> word.contexts.all { it.mediaFile == null } }

                WordListType.Recommendation -> {
                    val wordContextIds =
                        wordRepository.cachedRecommendedWords.map { it.contexts.single().wordContext.id }
                    // since recommendedWords may not be latest, we use words to filter
                    // to latest ones and still keep each word has one and only one wordContext
                    words
                        .flatMap { wordWithContexts ->
                            wordWithContexts.contexts.map {
                                Pair(wordWithContexts.word, it)
                            }
                        }
                        .filter { it.second.wordContext.id in wordContextIds }
                        .map {
                            WordWithContexts(
                                word = it.first,
                                contexts = listOf(it.second)
                            )
                        }
                }
            }
            val wordProficiencyType = route.wordProficiencyType ?: data.proficiencyType

            val selector =
                getWordSelector(options.sortMode, options.wordSortBy, wordProficiencyType)

            WordListUiState.Success(
                wordSortBy = options.wordSortBy,
                data = data.sortedBy(selector),
                proficiencyType = wordProficiencyType,
                wordListType = route.wordListType,
                id = route.id
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = WordListUiState.Loading
    )

    private fun getWordSelector(
        sortMode: SortMode,
        wordSortBy: WordSortBy,
        wordProficiencyType: WordProficiencyType,
    ): (WordWithContexts) -> Long {
        val sortModeFactor = when (sortMode) {
            SortMode.Ascending -> 1
            SortMode.Descending -> -1
        }

        return {
            when (wordSortBy) {
                WordSortBy.LastViewedDate -> (it.word.lastViewedDate?.toEpochMilliseconds()
                    ?: 0L) * sortModeFactor

                WordSortBy.Proficiency -> it.word.proficiency(wordProficiencyType).level.toLong() * sortModeFactor
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

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            wordRepository.deleteWord(word)
        }
    }
}

internal sealed interface WordOptionUiState {
    data object Loading : WordOptionUiState
    data class Success(
        val sortMode: SortMode,
        val wordSortBy: WordSortBy
    ) : WordOptionUiState
}

internal sealed interface WordListUiState {
    data object Loading : WordListUiState
    data object Empty : WordListUiState
    data class Success(
        val wordSortBy: WordSortBy,
        val data: List<WordWithContexts>,
        val proficiencyType: WordProficiencyType,
        val wordListType: WordListType,
        val id: String?
    ) : WordListUiState
}