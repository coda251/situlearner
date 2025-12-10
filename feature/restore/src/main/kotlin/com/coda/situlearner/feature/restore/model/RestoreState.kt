package com.coda.situlearner.feature.restore.model

internal sealed interface RestoreState {
    data object Idle : RestoreState
    data object Running : RestoreState
    data object Success : RestoreState
    data class Error(val message: String) : RestoreState
}