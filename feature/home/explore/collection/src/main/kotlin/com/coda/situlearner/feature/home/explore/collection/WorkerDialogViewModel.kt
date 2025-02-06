package com.coda.situlearner.feature.home.explore.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.cfg.LanguageConfig
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.SourceCollectionWithFiles
import com.coda.situlearner.core.model.infra.mapper.asMediaCollectionWithFiles
import com.coda.situlearner.feature.home.explore.collection.util.UpdateCollectionWithFilesWorker
import com.coda.situlearner.infra.subkit.processor.Processor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class WorkerDialogViewModel(
    private val mediaRepository: MediaRepository,
    private val processor: Processor
) : ViewModel() {

    private val _workerStatusUiState: MutableStateFlow<WorkerStatusUiState> =
        MutableStateFlow(WorkerStatusUiState.Idle)
    val workerUiState = _workerStatusUiState.asStateFlow()

    @OptIn(ExperimentalUuidApi::class)
    fun insert(
        collectionWithFiles: SourceCollectionWithFiles,
        sourceLanguage: Language,
        targetLanguage: Language = LanguageConfig.targetLanguage,
    ) {
        viewModelScope.launch {
            _workerStatusUiState.value = WorkerStatusUiState.Ongoing

            val id = collectionWithFiles.collection.idInDb ?: Uuid.random().toString()
            mediaRepository.insertMediaCollectionWithFiles(
                collectionWithFiles.asMediaCollectionWithFiles(
                    collectionId = id
                )
            )

            // TODO: consider more callbacks to indicate which stage the worker is now on
            val worker = UpdateCollectionWithFilesWorker(
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                collectionId = id,
                mediaRepository = mediaRepository,
                processor = processor,
            )
            worker.doWork()

            _workerStatusUiState.value = WorkerStatusUiState.Done
        }
    }
}

internal sealed interface WorkerStatusUiState {
    data object Idle : WorkerStatusUiState
    data object Ongoing : WorkerStatusUiState
    data object Done : WorkerStatusUiState
}