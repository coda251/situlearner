package com.coda.situlearner.feature.word.detail.relation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.feature.word.detail.relation.model.MatchSimilarityType
import com.coda.situlearner.feature.word.detail.relation.model.WordMatchResult
import com.coda.situlearner.feature.word.detail.relation.model.matchWords
import com.coda.situlearner.feature.word.detail.relation.navigation.WordDetailRelationRoute
import com.coda.situlearner.infra.subkit.matcher.Matcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class WordRelationViewModel(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository
) : ViewModel() {
    private val route = savedStateHandle.toRoute<WordDetailRelationRoute>()

    private val _rawMatches = MutableStateFlow<MatchResultUiState>(MatchResultUiState.Loading)

    private val _filterUiState = MutableStateFlow(MatchFilterUiState())
    val filterUiState = _filterUiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MatchResultUiState> = combine(
        _rawMatches,
        _filterUiState
    ) { raw, filter -> raw to filter }.mapLatest { (raw, filter) ->
        when (raw) {
            MatchResultUiState.Loading -> MatchResultUiState.Loading
            MatchResultUiState.Empty -> MatchResultUiState.Empty
            is MatchResultUiState.Success -> {
                MatchResultUiState.Success(
                    query = raw.query,
                    words = raw.words.filter {
                        when (filter.similarityType) {
                            MatchSimilarityType.Comprehensive ->
                                it.similarity >= filter.threshold

                            MatchSimilarityType.Lemma ->
                                it.lemmaSimilarity >= filter.threshold

                            MatchSimilarityType.Pronunciation ->
                                it.pronunciationSimilarity >= filter.threshold
                        }
                    }.sortedByDescending {
                        when (filter.similarityType) {
                            MatchSimilarityType.Comprehensive -> it.similarity
                            MatchSimilarityType.Lemma -> it.lemmaSimilarity
                            MatchSimilarityType.Pronunciation -> it.pronunciationSimilarity
                        }
                    }.take(50)
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MatchResultUiState.Loading
    )

    init {
        getWords()
    }

    private fun getWords() {
        viewModelScope.launch {
            val query = wordRepository.getWord(route.wordId)
            if (query == null) {
                _rawMatches.value = MatchResultUiState.Empty
                return@launch
            }

            wordRepository.words
                .map { targets ->
                    val matcher = Matcher.getMatcher(query.language)
                    matchWords(
                        query = query,
                        targets = targets.map { it.word },
                        matcher = matcher,
                    ).filter { it.id != query.id }

                }
                .flowOn(Dispatchers.Default)
                .collect { data ->
                    _rawMatches.value = MatchResultUiState.Success(
                        query = query,
                        words = data,
                        version = System.nanoTime()
                    )
                }
        }
    }

    fun setSimilarityType(type: MatchSimilarityType) {
        _filterUiState.value = _filterUiState.value.copy(similarityType = type)
    }

    fun setThreshold(threshold: Double) {
        _filterUiState.value = _filterUiState.value.copy(threshold = threshold)
    }
}

internal sealed interface MatchResultUiState {
    data object Loading : MatchResultUiState
    data object Empty : MatchResultUiState
    data class Success(
        val query: Word,
        val words: List<WordMatchResult>,
        val version: Long = System.currentTimeMillis()
    ) : MatchResultUiState
}

internal data class MatchFilterUiState(
    val similarityType: MatchSimilarityType = MatchSimilarityType.Comprehensive,
    val threshold: Double = 0.4
)