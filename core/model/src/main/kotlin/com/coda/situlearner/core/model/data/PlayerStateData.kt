package com.coda.situlearner.core.model.data

data class PlayerStateData(
    val repeatMode: RepeatMode,
    val positionInMs: Long,
    val playlist: Playlist,
)