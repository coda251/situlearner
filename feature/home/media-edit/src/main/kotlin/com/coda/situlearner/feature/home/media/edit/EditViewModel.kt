package com.coda.situlearner.feature.home.media.edit

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.feature.home.media.edit.domain.ExtractBitmapUseCase
import com.coda.situlearner.feature.home.media.edit.navigation.HomeMediaEditRoute
import com.coda.situlearner.infra.explorer.local.util.downscale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class EditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MediaRepository,
    private val extractBitmapUseCase: ExtractBitmapUseCase
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

    fun updateCollection(collection: MediaCollection) {
        viewModelScope.launch {
            _uiState.value = UiState.Saving
            repository.updateMediaCollection(collection)
            collection.originalCoverImageUrl?.toUri()?.let {
                extractBitmapUseCase(it)
            }?.let {
                repository.cacheCoverImage(collection.id, it.downscale())
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