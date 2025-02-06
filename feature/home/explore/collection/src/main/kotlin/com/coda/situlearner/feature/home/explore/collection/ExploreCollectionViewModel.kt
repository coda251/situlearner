package com.coda.situlearner.feature.home.explore.collection

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.model.infra.SourceCollection
import com.coda.situlearner.core.model.infra.SourceCollectionWithFiles
import com.coda.situlearner.core.model.infra.mapper.resolveId
import com.coda.situlearner.feature.home.explore.collection.navigation.ExploreCollectionRoute
import com.coda.situlearner.infra.explorer_local.LocalExplorer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform

internal class ExploreCollectionViewModel(
    savedStateHandle: SavedStateHandle,
    private val localExplorer: LocalExplorer,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ExploreCollectionRoute>()

    val uiState = flowOf(route.url).transform { url ->
        val path = Uri.parse(url).path
        if (path == null) emit(ExploreCollectionUiState.Error)
        else emitAll(
            combine(
                mediaRepository.getMediaCollectionWithFilesByUrl(url),
                localExplorer.getSourceCollectionWithFiles(path)
            ) { mediaCollectionWithFiles, sourceCollectionWithFiles ->
                mediaCollectionWithFiles?.let {
                    sourceCollectionWithFiles.resolveId(it)
                } ?: sourceCollectionWithFiles
            }.map {
                if (it.files.isEmpty()) ExploreCollectionUiState.Empty(it.collection)
                else ExploreCollectionUiState.Success(it)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ExploreCollectionUiState.Loading
    )
}

internal sealed interface ExploreCollectionUiState {
    data object Loading : ExploreCollectionUiState
    data object Error : ExploreCollectionUiState
    data class Empty(val collection: SourceCollection) : ExploreCollectionUiState
    data class Success(val collectionWithFiles: SourceCollectionWithFiles) :
        ExploreCollectionUiState
}