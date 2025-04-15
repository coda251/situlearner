package com.coda.situlearner.feature.word.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.feature.word.edit.navigation.WordEditRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class WordEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
): ViewModel() {

    private val route = savedStateHandle.toRoute<WordEditRoute>()

    private val _uiState = MutableStateFlow<WordEditUiState>(WordEditUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getWord()
    }

    private fun getWord() {
        viewModelScope.launch {
            _uiState.value = WordEditUiState.Loading
            _uiState.value = withContext(Dispatchers.IO) {
                wordRepository.getWord(route.wordId)?.let {
                    WordEditUiState.Success(it)
                } ?: WordEditUiState.Empty
            }
        }
    }

    fun updateWord(word: Word) {
        viewModelScope.launch {
            _uiState.value = WordEditUiState.Saving
            wordRepository.updateWord(word)
            _uiState.value = WordEditUiState.Saved
        }
    }
}

internal sealed interface WordEditUiState {
    data object Loading: WordEditUiState
    data object Empty: WordEditUiState
    data class Success(val word: Word): WordEditUiState
    data object Saving: WordEditUiState
    data object Saved: WordEditUiState
}