package com.coda.situlearner.core.model.infra

sealed interface RemoteWordInfoState {
    data object Loading : RemoteWordInfoState
    data object Error : RemoteWordInfoState
    data object Empty : RemoteWordInfoState
    data class Single(val info: WordInfo) : RemoteWordInfoState
    data class Multiple(val infos: List<WordInfo>) : RemoteWordInfoState
}