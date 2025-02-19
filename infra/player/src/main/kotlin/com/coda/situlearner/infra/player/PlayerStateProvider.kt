package com.coda.situlearner.infra.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.coda.situlearner.core.model.data.PlayerStateData
import com.coda.situlearner.core.model.data.Playlist
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.model.data.PlaylistType
import com.coda.situlearner.core.model.data.RepeatMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object PlayerStateProvider {

    // other ways to get state from service to composable?
    // 1. CompositionLocalProvider

    private val _state: MutableStateFlow<PlayerState> = MutableStateFlow(EmptyPlayerState)
    val state = _state.asStateFlow()

    fun register(state: PlayerState?) {
        _state.value = state ?: EmptyPlayerState
    }

    fun unregister() {
        _state.value = EmptyPlayerState
    }

    object EmptyPlayerState : PlayerState {
        override val repeatMode = MutableStateFlow(RepeatMode.One).asStateFlow()
        override val positionInMs = MutableStateFlow(0L).asStateFlow()
        override val durationInMs = MutableStateFlow<Long?>(null).asStateFlow()
        override val isPlaying = MutableStateFlow(false).asStateFlow()
        override val loopInMs = MutableStateFlow(Pair(null, null)).asStateFlow()
        override val playlist = MutableStateFlow(Playlist()).asStateFlow()
        override val playlistType = MutableStateFlow(PlaylistType.Temporary).asStateFlow()

        override fun play() {}
        override fun pause() {}
        override fun playNext() {}
        override fun playPrevious() {}
        override fun seekTo(position: Long) {}
        override fun seekToItem(index: Int) {}
        override fun seekToItem(item: PlaylistItem) {}
        override fun setRepeatMode(mode: RepeatMode) {}
        override fun setPlaybackSpeed(speed: Float) {}
        override fun setPlaybackLoop(start: Long?, end: Long?) {}
        override fun setRepeatNumber(number: Int) {}

        override fun addItems(items: List<PlaylistItem>, startItem: PlaylistItem?) {}
        override fun setItems(items: List<PlaylistItem>) {}

        override fun removeItem(index: Int) {}
        override fun clear() {}
        override fun swapItems(fromIndex: Int, toIndex: Int) {}
        override fun shufflePlaylist() {}
        override fun restoreFrom(data: PlayerStateData) {}
        override fun switchPlaylistType(type: PlaylistType) {}

        @Composable
        override fun VideoOutput(modifier: Modifier) {
        }
    }
}