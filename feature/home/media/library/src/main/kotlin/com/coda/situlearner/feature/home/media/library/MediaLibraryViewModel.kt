package com.coda.situlearner.feature.home.media.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.model.data.MediaCollection
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class MediaLibraryViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    val uiState: StateFlow<MediaLibraryUiState> = mediaRepository.getMediaCollections().map {
        if (it.isEmpty()) MediaLibraryUiState.Empty
        else MediaLibraryUiState.Success(collections = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MediaLibraryUiState.Loading
    )

    fun deleteMediaCollection(mediaCollection: MediaCollection) {
        viewModelScope.launch {
            mediaRepository.deleteMediaCollection(mediaCollection.id)
        }
    }

    fun setMediaCollectionName(mediaCollection: MediaCollection, name: String) {
        viewModelScope.launch {
            mediaRepository.setMediaCollectionName(mediaCollection.id, name)
        }
    }
}

internal sealed interface MediaLibraryUiState {
    data object Empty : MediaLibraryUiState
    data object Loading : MediaLibraryUiState
    data class Success(val collections: List<MediaCollection>) : MediaLibraryUiState
}