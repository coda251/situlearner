package com.coda.situlearner.feature.home.settings.common.model

internal sealed interface VersionState {
    data object NotChecked : VersionState
    data object Loading : VersionState
    data object Failed : VersionState
    data object UpToDate : VersionState
    data class UpdateAvailable(
        val version: String,
        val downloadUrl: String,
    ) : VersionState
}