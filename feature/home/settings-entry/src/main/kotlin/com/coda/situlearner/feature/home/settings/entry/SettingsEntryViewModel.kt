package com.coda.situlearner.feature.home.settings.entry

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AppVersionRepository
import com.coda.situlearner.feature.home.settings.entry.domain.ExportDataUseCase
import com.coda.situlearner.feature.home.settings.entry.model.ExportState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class SettingsEntryViewModel(
    private val appVersionRepository: AppVersionRepository,
    private val exportDataUseCase: ExportDataUseCase,
) : ViewModel() {

    val currentVersion = appVersionRepository.currentVersion
    val versionState = appVersionRepository.appVersionState

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState = _exportState.asStateFlow()

    fun checkAppUpdate() {
        viewModelScope.launch {
            appVersionRepository.checkAppUpdate()
        }
    }

    fun exportData(uri: Uri) {
        viewModelScope.launch {
            _exportState.value = ExportState.Running
            _exportState.value = exportDataUseCase(uri)
        }
    }

    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }
}