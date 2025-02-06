package com.coda.situlearner.core.datastore

import androidx.datastore.core.DataStore

internal class LocalPlayerStateDataSource(private val playerState: DataStore<PlayerStateProto>) :
    PlayerStateDataSource {

    override val playerStateProto = playerState.data

    override suspend fun setPositionInMsProto(positionInMs: Long) {
        playerState.updateData {
            it.copy {
                this.positionInMs = positionInMs
            }
        }
    }

    override suspend fun setPlaylistProto(itemsProto: List<PlaylistItemProto>, index: Int) {
        playerState.updateData {
            it.copy {
                this.items.clear()
                this.items.addAll(itemsProto)
                this.currentIndex = index
            }
        }
    }

    override suspend fun setRepeatModeProto(repeatModeProto: RepeatModeProto) {
        playerState.updateData {
            it.copy {
                this.repeatMode = repeatModeProto
            }
        }
    }
}