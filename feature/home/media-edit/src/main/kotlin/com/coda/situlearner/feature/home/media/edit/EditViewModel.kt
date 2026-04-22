package com.coda.situlearner.feature.home.media.edit

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.ui.util.extractBitmapFrom
import com.coda.situlearner.feature.home.media.edit.navigation.HomeMediaEditRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class EditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MediaRepository,
    private val application: Application,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<HomeMediaEditRoute>()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getCollection()
    }

    private fun getCollection() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val collection = repository.getMediaCollection(route.collectionId)
            _uiState.value = if (collection == null) {
                UiState.Empty
            } else {
                UiState.Success(collection)
            }
        }
    }

    fun updateCollection(cur: MediaCollection, orig: MediaCollection) {
        viewModelScope.launch {
            _uiState.value = UiState.Saving
            repository.updateMediaCollection(cur)
            if (cur.originalCoverImageUrl != orig.originalCoverImageUrl) {
                cur.originalCoverImageUrl?.let {
                    extractBitmapFrom(application, it, false)
                }?.let {
                    repository.cacheCoverImage(cur.id, it)
                }
            }
            _uiState.value = UiState.Saved
        }
    }
}

internal sealed interface UiState {
    data object Loading : UiState
    data class Success(val collection: MediaCollection) : UiState
    data object Empty : UiState
    data object Saving : UiState
    data object Saved : UiState
}