package com.coda.situlearner.infra.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.coda.situlearner.core.model.data.PlayerStateData
import com.coda.situlearner.core.model.data.Playlist
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.model.data.PlaylistType
import com.coda.situlearner.core.model.data.RepeatMode
import kotlinx.coroutines.flow.StateFlow

interface PlayerState {

    companion object {
        const val TIME_UNSET = -1L
    }

    val repeatMode: StateFlow<RepeatMode>

    val positionInMs: StateFlow<Long>

    val durationInMs: StateFlow<Long?>

    val isPlaying: StateFlow<Boolean>

    val playWhenReady: StateFlow<Boolean>

    val loopInMs: StateFlow<Pair<Long?, Long?>>

    fun play()

    fun pause()

    fun playNext()

    fun playPrevious()

    fun seekTo(position: Long)

    fun seekToItem(index: Int)

    fun seekToItem(item: PlaylistItem)

    fun setRepeatMode(mode: RepeatMode)

    fun setPlaybackSpeed(speed: Float)

    /**
     * start:
     *  - null: do not change the original loop start
     *  - TIME_UNSET: unset the loop start
     *  - 0 ~ duration: the valid loop start
     */
    fun setPlaybackLoop(start: Long?, end: Long?)

    /**
     * Items will be played for `number` times when the repeat mode
     * is [RepeatMode.All][RepeatMode.All].
     */
    fun setRepeatNumber(number: Int)

    val playlist: StateFlow<Playlist>

    val playlistType: StateFlow<PlaylistType>

    fun addItems(items: List<PlaylistItem>, startItem: PlaylistItem?)

    fun setItems(items: List<PlaylistItem>)

    fun removeItem(index: Int)

    fun clear()

    fun swapItems(fromIndex: Int, toIndex: Int)

    fun shufflePlaylist()

    fun restoreFrom(data: PlayerStateData)

    /**
     * This function should be handled by the player container which
     * is responsible for managing player state persistence.
     */
    fun switchPlaylistType(type: PlaylistType)

    @Composable
    fun VideoOutput(modifier: Modifier)
}