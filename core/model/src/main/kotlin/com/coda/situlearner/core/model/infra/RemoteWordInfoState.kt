package com.coda.situlearner.core.model.infra

sealed interface RemoteWordInfoState {
    data object Loading : RemoteWordInfoState
    data object Error : RemoteWordInfoState

    /**
     * No such word info or all word infos are empty.
     */
    data object Empty : RemoteWordInfoState

    /**
     * Only one non-empty ([WordInfo.isNotEmpty]) word info is available.
     */
    data class Single(val info: WordInfo) : RemoteWordInfoState

    /**
     * Multiple non-empty ([WordInfo.isNotEmpty]) word infos are available.
     * Words of these infos are distinct.
     */
    data class Multiple(val infos: List<WordInfo>) : RemoteWordInfoState
}