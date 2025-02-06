package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.model.data.PlayerStateData
import com.coda.situlearner.core.model.data.Playlist
import com.coda.situlearner.core.model.data.RepeatMode
import kotlinx.coroutines.flow.Flow

interface PlayerStateRepository {

    val playerStateData: Flow<PlayerStateData>

    suspend fun setPositionInMs(position: Long)

    suspend fun setPlaylist(playlist: Playlist)

    suspend fun setRepeatMode(repeatMode: RepeatMode)
}