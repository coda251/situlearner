package com.coda.situlearner.feature.home.settings.entry

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.feature.home.settings.entry.domain.ExportDataUseCase
import com.coda.situlearner.feature.home.settings.entry.model.ExportState
import com.coda.situlearner.feature.home.settings.entry.model.VersionState
import com.coda.situlearner.feature.home.settings.entry.util.getRelease
import com.coda.situlearner.feature.home.settings.entry.util.toVersionState
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SettingsEntryViewModel(
    private val client: HttpClient,
    private val exportDataUseCase: ExportDataUseCase,
) : ViewModel() {
    private val _versionState = MutableStateFlow<VersionState>(VersionState.NotChecked)
    val versionState = _versionState.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState = _exportState.asStateFlow()

    fun checkAppUpdate(currentVersion: String?) {
        viewModelScope.launch {
            _versionState.value = VersionState.Loading
            _versionState.value = withContext(Dispatchers.IO) {
                try {
                    getRelease(client).toVersionState(currentVersion)
                } catch (_: Exception) {
                    VersionState.Failed
                }
            }
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