package com.coda.situlearner.feature.home.settings.entry.model

sealed interface ExportState {
    data object Idle : ExportState
    data object Running : ExportState
    data class Error(val message: String) : ExportState
    data object Success : ExportState
}