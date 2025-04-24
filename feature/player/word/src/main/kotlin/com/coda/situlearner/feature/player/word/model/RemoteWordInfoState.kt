package com.coda.situlearner.feature.player.word.model

import com.coda.situlearner.core.model.infra.WordInfo

sealed interface RemoteWordInfoState {
    data object Loading : RemoteWordInfoState
    data object Error : RemoteWordInfoState
    data object Empty : RemoteWordInfoState
    data class Single(val info: WordInfo) : RemoteWordInfoState
    data class Multiple(val infos: List<WordInfo>) : RemoteWordInfoState
}