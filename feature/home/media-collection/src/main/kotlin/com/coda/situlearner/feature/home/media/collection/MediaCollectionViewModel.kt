package com.coda.situlearner.feature.home.media.collection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.MediaCollectionWithFiles
import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.feature.home.media.collection.navigation.HomeMediaCollectionRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class MediaCollectionViewModel(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    private val route = savedStateHandle.toRoute<HomeMediaCollectionRoute>()

    val uiState: StateFlow<MediaCollectionUiState> =
        mediaRepository.getMediaCollectionWithFilesById(
            route.collectionId
        ).map { collectionWithFiles ->
            collectionWithFiles?.let {
                MediaCollectionUiState.Success(
                    collection = it.collection,
                    files = it.files
                )
            } ?: MediaCollectionUiState.Error
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MediaCollectionUiState.Loading
        )

    fun deleteMediaCollection() {
        viewModelScope.launch {
            _actionState.value = ActionState.Deleting
            mediaRepository.deleteMediaCollection(route.collectionId)
            _actionState.value = ActionState.Deleted
        }
    }

    fun setMediaCollectionName(name: String) {
        viewModelScope.launch {
            mediaRepository.setMediaCollectionName(route.collectionId, name)
        }
    }
}

internal sealed interface MediaCollectionUiState {
    data object Loading : MediaCollectionUiState
    data object Error : MediaCollectionUiState
    data class Success(
        val collection: MediaCollection,
        val files: List<MediaFile>
    ) : MediaCollectionUiState {
        fun asMediaCollectionWithFiles() = MediaCollectionWithFiles(
            collection = collection,
            files = files
        )
    }
}

internal sealed interface ActionState {
    data object Idle : ActionState
    data object Deleting : ActionState
    data object Deleted : ActionState
}