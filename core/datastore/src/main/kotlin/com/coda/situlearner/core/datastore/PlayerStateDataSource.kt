package com.coda.situlearner.core.datastore

import kotlinx.coroutines.flow.Flow

interface PlayerStateDataSource {

    val playerStateProto: Flow<PlayerStateProto>

    suspend fun setPositionInMsProto(positionInMs: Long)

    suspend fun setPlaylistProto(itemsProto: List<PlaylistItemProto>, index: Int)

    suspend fun setRepeatModeProto(repeatModeProto: RepeatModeProto)
}