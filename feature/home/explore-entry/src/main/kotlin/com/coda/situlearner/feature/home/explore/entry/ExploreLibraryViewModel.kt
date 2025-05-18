package com.coda.situlearner.feature.home.explore.entry

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.model.infra.SourceCollection
import com.coda.situlearner.infra.explorer.local.LocalExplorer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class ExploreLibraryViewModel(
    private val localExplorer: LocalExplorer,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    val videoUiState =
        getUiState(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).path)

    val audioUiState =
        getUiState(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path)

    private fun getUiState(path: String) = getExploreLibrary(path).map {
        if (it.isEmpty()) ExploreLibraryUiState.Empty
        else ExploreLibraryUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ExploreLibraryUiState.Loading
    )

    private fun getExploreLibrary(path: String) = combine(
        mediaRepository.getMediaCollections(),
        localExplorer.getSourceCollections(path),
    ) { mediaCollections, sourceCollections ->
        val urlToId = mediaCollections.associate { Pair(it.url, it.id) }

        sourceCollections.map {
            it.copy(idInDb = urlToId.getOrDefault(it.url, null))
        }
    }
}

internal sealed interface ExploreLibraryUiState {
    data object Loading : ExploreLibraryUiState
    data object Empty : ExploreLibraryUiState
    data class Success(val collections: List<SourceCollection>) : ExploreLibraryUiState
}