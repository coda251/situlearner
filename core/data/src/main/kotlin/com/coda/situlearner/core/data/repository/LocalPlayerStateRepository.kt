package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.data.mapper.asExternalModel
import com.coda.situlearner.core.data.mapper.asProto
import com.coda.situlearner.core.datastore.PlayerStateDataSource
import com.coda.situlearner.core.datastore.PlayerStateProto
import com.coda.situlearner.core.model.data.PlayerStateData
import com.coda.situlearner.core.model.data.Playlist
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.model.data.RepeatMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LocalPlayerStateRepository(private val dataSource: PlayerStateDataSource) :
    PlayerStateRepository {

    override val playerStateData: Flow<PlayerStateData>
        get() = dataSource.playerStateProto.map(PlayerStateProto::asExternalModel)

    override suspend fun setPositionInMs(position: Long) {
        dataSource.setPositionInMsProto(position)
    }

    override suspend fun setPlaylist(playlist: Playlist) {
        dataSource.setPlaylistProto(
            itemsProto = playlist.items.map(PlaylistItem::asProto),
            index = playlist.currentIndex
        )
    }

    override suspend fun setRepeatMode(repeatMode: RepeatMode) {
        dataSource.setRepeatModeProto(repeatMode.asProto())
    }
}