package com.coda.situlearner.feature.restore

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.feature.restore.domain.RestoreDataUseCase
import com.coda.situlearner.feature.restore.model.RestoreState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class RestoreViewModel(
    private val restoreDataUseCase: RestoreDataUseCase
) : ViewModel() {

    private val _restoreState = MutableStateFlow<RestoreState>(RestoreState.Idle)
    val restoreState = _restoreState.asStateFlow()

    fun restoreData(uri: Uri) {
        viewModelScope.launch {
            _restoreState.value = RestoreState.Running
            _restoreState.value = restoreDataUseCase(uri)
        }
    }

    fun resetRestoreState() {
        _restoreState.value = RestoreState.Idle
    }
}