package com.coda.situlearner.feature.home.media.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.model.data.MediaCollection
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class MediaLibraryViewModel(mediaRepository: MediaRepository) : ViewModel() {

    val uiState: StateFlow<MediaLibraryUiState> = mediaRepository.getMediaCollections().map {
        if (it.isEmpty()) MediaLibraryUiState.Empty
        else MediaLibraryUiState.Success(collections = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MediaLibraryUiState.Loading
    )
}

internal sealed interface MediaLibraryUiState {
    data object Empty : MediaLibraryUiState
    data object Loading : MediaLibraryUiState
    data class Success(val collections: List<MediaCollection>) : MediaLibraryUiState
}