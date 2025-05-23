package com.coda.situlearner.feature.home.media.collection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.MediaCollectionWithFiles
import com.coda.situlearner.feature.home.media.collection.navigation.HomeMediaCollectionRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class MediaCollectionViewModel(
    savedStateHandle: SavedStateHandle,
    mediaRepository: MediaRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<HomeMediaCollectionRoute>()

    val uiState: StateFlow<MediaCollectionUiState> =
        mediaRepository.getMediaCollectionWithFilesById(
            route.collectionId
        ).map { collectionWithFiles ->
            collectionWithFiles?.let {
                if (it.files.isEmpty()) MediaCollectionUiState.Empty(collection = it.collection)
                else MediaCollectionUiState.Success(it)
            } ?: MediaCollectionUiState.Error
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MediaCollectionUiState.Loading
        )
}

internal sealed interface MediaCollectionUiState {
    data object Loading : MediaCollectionUiState
    data object Error : MediaCollectionUiState
    data class Empty(val collection: MediaCollection) : MediaCollectionUiState
    data class Success(val collectionWithFiles: MediaCollectionWithFiles) : MediaCollectionUiState
}