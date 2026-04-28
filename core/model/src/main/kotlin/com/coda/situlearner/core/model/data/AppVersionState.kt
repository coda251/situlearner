package com.coda.situlearner.core.model.data

sealed interface AppVersionState {
    data object NotChecked : AppVersionState
    data object Loading : AppVersionState
    data object Failed : AppVersionState
    data object UpToDate : AppVersionState
    data class UpdateAvailable(
        val version: String,
        val downloadUrl: String,
    ) : AppVersionState
}