package com.coda.situlearner.infra.player.exoplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.coda.situlearner.core.model.data.PlayerStateData
import com.coda.situlearner.core.model.data.Playlist
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.model.data.PlaylistType
import com.coda.situlearner.core.model.data.RepeatMode
import com.coda.situlearner.infra.player.PlayerEngine
import com.coda.situlearner.infra.player.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

internal class ExoPlayerEngine(
    context: Context,
    private val playlistManager: ExoPlaylistManager,
    override val scope: CoroutineScope
) : PlayerEngine {

    private var shouldUpdateDuration = true

    private var loop = LoopRange()

    private var isOnSeekingProgress = false

    private var repeatNumber = 1

    private var remainingRepeatNumber = repeatNumber

    private var updatePositionAndCheckLoopPlayingJob = scope.launch(
        start = CoroutineStart.LAZY
    ) {
        while (this.isActive) {
            delay(100)

            // assure the player is playing in the loop
            if (loop.isEndSet && player.currentPosition > loop.end) {
                if (player.isPlaying) {
                    player.seekTo(if (loop.isStartSet) loop.start else 0)
                }
            }
            if (loop.isStartSet && player.currentPosition < loop.start) {
                player.seekTo(loop.start)
            }

            // update progress
            if (!isOnSeekingProgress) {
                positionInMs.value = player.currentPosition
            }
        }
    }

    private val player = ExoPlayer.Builder(context).apply {
        setHandleAudioBecomingNoisy(true)
        setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            true
        )
    }.build().apply {
        addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                when (reason) {
                    // when new items are added
                    Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                        playlistManager.recentItem = mediaItem

                        if (this@apply.duration == C.TIME_UNSET) {
                            durationInMs.value = null
                            shouldUpdateDuration = true
                        } else {
                            durationInMs.value = this@apply.duration
                        }
                    }

                    else -> {}
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        isOnSeekingProgress = false
                        if (shouldUpdateDuration) {
                            durationInMs.value = this@apply.duration
                            shouldUpdateDuration = false
                        }
                    }

                    Player.STATE_ENDED -> {
                        if (loop.isSet)
                            seekTo(if (loop.isStartSet) loop.start else 0)
                        else {
                            // if size == 1, then there will be no next item. we go
                            // back to 0 position so the media continues
                            if (playlistManager.items.size == 1) seekTo(0L)
                            else {
                                remainingRepeatNumber--
                                if (remainingRepeatNumber > 0) {
                                    seekTo(0L)
                                } else {
                                    playNext()
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                this@ExoPlayerEngine.isPlaying.value = isPlaying
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    isOnSeekingProgress = true
                    positionInMs.value = this@apply.currentPosition
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                this@ExoPlayerEngine.playWhenReady.value = playWhenReady
            }
        })
    }

    override fun onCreate() {
        updatePositionAndCheckLoopPlayingJob.start()
    }

    override fun onDestroy() {
        updatePositionAndCheckLoopPlayingJob.cancel()

        player.stop()
        player.release()
    }

    override val repeatMode = MutableStateFlow(RepeatMode.All)
    override val positionInMs: MutableStateFlow<Long> = MutableStateFlow(0)
    override val durationInMs: MutableStateFlow<Long?> = MutableStateFlow(null)
    override val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val playWhenReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val loopInMs: MutableStateFlow<Pair<Long?, Long?>> = MutableStateFlow(Pair(null, null))

    override fun play() {
        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun playNext() {
        playlistManager.nextItem?.let {
            seekTo(it)
        }
    }

    override fun playPrevious() {
        playlistManager.prevItem?.let {
            seekTo(it)
        }
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
    }

    override fun seekToItem(index: Int) {
        playlistManager.items.getOrNull(index)?.let {
            seekTo(it)
        }
    }

    override fun seekToItem(item: PlaylistItem) {
        playlistManager.items.firstOrNull { it.mediaId == item.id }?.let {
            seekTo(it)
        }
    }

    override fun setRepeatMode(mode: RepeatMode) {
        when (mode) {
            RepeatMode.One -> player.repeatMode = Player.REPEAT_MODE_ONE
            // here we use a single item exoplayer, so let the playlist manager decides
            // the next item and set repeatMode to OFF
            RepeatMode.All -> player.repeatMode = Player.REPEAT_MODE_OFF
        }

        repeatMode.value = mode
    }

    override fun setPlaybackSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
    }

    override fun setPlaybackLoop(start: Long?, end: Long?) {
        loop = loop.reset(start, end)
        loopInMs.value = Pair(
            if (loop.isStartSet) loop.start else null,
            if (loop.isEndSet) loop.end else null
        )
    }

    override fun setRepeatNumber(number: Int) {
        max(1, number).let {
            if (it != repeatNumber) {
                repeatNumber = it
                remainingRepeatNumber = it
            }
        }
    }

    override val playlist: MutableStateFlow<Playlist> = MutableStateFlow(Playlist())
    override val playlistType: MutableStateFlow<PlaylistType> =
        MutableStateFlow(PlaylistType.Persistent)

    override fun addItems(items: List<PlaylistItem>, startItem: PlaylistItem?) {
        playlistManager.add(items.map(PlaylistItem::asMediaItem))

        startItem?.let { s ->
            playlistManager.items.firstOrNull { it.mediaId == s.id }?.let {
                // a valid non-null startItem
                if (it == player.currentMediaItem) updatePlaylist()
                else seekTo(it) // let seekTo handle updatePlaylist()
            } ?: run { updatePlaylist() }
        } ?: run {
            if (player.currentMediaItem == null) {
                // start from the first one if no item is playing
                playlistManager.items.firstOrNull()?.let { seekTo(it) }
            } else updatePlaylist()
        }
    }

    override fun setItems(items: List<PlaylistItem>) {
        if (items.isEmpty()) {
            clear()
            return
        }

        player.clearMediaItems()
        playlistManager.clear()
        setPlaybackLoop(PlayerState.TIME_UNSET, PlayerState.TIME_UNSET)
        addItems(items, items[0])
    }

    override fun removeItem(index: Int) {
        playlistManager.items.getOrNull(index)?.let {
            remove(it)
        }
    }

    override fun clear() {
        player.clearMediaItems()
        playlistManager.clear()
        updatePlaylist()
        setPlaybackLoop(PlayerState.TIME_UNSET, PlayerState.TIME_UNSET)
    }

    override fun swapItems(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        playlistManager.swap(fromIndex, toIndex)
        updatePlaylist()
    }

    override fun shufflePlaylist() {
        playlistManager.shuffle()
        updatePlaylist()
    }

    override fun restoreFrom(data: PlayerStateData) {
        val currentIndex = data.playlist.currentIndex
        val items = data.playlist.mapIndexed { index, it ->
            if (index == currentIndex) it.copy(lastPositionInMs = data.positionInMs)
            else it
        }

        setRepeatMode(data.repeatMode)
        addItems(items, items.getOrNull(currentIndex))
    }

    override fun switchPlaylistType(type: PlaylistType) {
        playlistType.value = type
    }

    @OptIn(UnstableApi::class)
    @Composable
    override fun VideoOutput(modifier: Modifier) {
        val presentationState = rememberPresentationState(player)

        PlayerSurface(
            player = player,
            modifier = modifier.resizeWithContentScale(
                ContentScale.Fit,
                presentationState.videoSizeDp
            ),
            // NOTE:
            // SurfaceView may cause frame retained in the screen during navigation,
            // see also: https://github.com/androidx/media/issues/1237
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW
        )
    }

    private fun seekTo(item: MediaItem) {
        if (item == player.currentMediaItem) return

        // update last played item if needed
        playlistManager.recentItem?.apply {
            putLong(
                MediaMetadataExtraKey.LAST_PLAY_POSITION,
                if (player.playbackState == Player.STATE_ENDED) PlayerState.TIME_UNSET
                else player.currentPosition
            )
        }

        // set current item
        player.setMediaItem(item, item.startPlayingPosition)
        remainingRepeatNumber = repeatNumber
        player.prepare()
        updatePlaylist()
        setPlaybackLoop(PlayerState.TIME_UNSET, PlayerState.TIME_UNSET)
    }

    private fun remove(item: MediaItem) {
        if (item == player.currentMediaItem) {
            playlistManager.nextItem?.let {
                player.setMediaItem(it)
                remainingRepeatNumber = repeatNumber
                player.prepare()
            } ?: run {
                player.clearMediaItems()
            }
        }

        playlistManager.remove(item)
        updatePlaylist()
        setPlaybackLoop(PlayerState.TIME_UNSET, PlayerState.TIME_UNSET)
    }

    private fun updatePlaylist() {
        playlist.value = Playlist(
            items = playlistManager.items.map { it.asPlaylistItem() },
            currentIndex = playlistManager.items.indexOf(player.currentMediaItem)
        )
    }
}

internal class ExoPlaylistManager {

    /**
     * Current playing or last played item.
     */
    var recentItem: MediaItem? = null

    val items = mutableListOf<MediaItem>()

    private val recentItemIndex
        get() = items.indexOf(recentItem)

    val nextItem: MediaItem?
        get() = if (recentItemIndex in items.indices) {
            if (items.size == 1) null
            else items.getOrElse(recentItemIndex + 1) { items.first() }
        } else null


    val prevItem: MediaItem?
        get() = if (recentItemIndex in items.indices) {
            if (items.size == 1) null
            else items.getOrElse(recentItemIndex - 1) { items.last() }
        } else null

    fun clear() {
        items.clear()
        recentItem = null
    }

    fun add(items: List<MediaItem>) {
        val newItems = filterRedundant(items)
        this.items.addAll(newItems)
    }

    fun remove(item: MediaItem) {
        items.remove(item)
        if (items.isEmpty()) recentItem = null
    }

    fun swap(fromIndex: Int, toIndex: Int) {
        if (fromIndex in items.indices && toIndex in items.indices) {
            items[fromIndex] = items.set(toIndex, items[fromIndex])
        }
    }

    fun shuffle() {
        items.shuffle()
    }

    private fun filterRedundant(items: List<MediaItem>): List<MediaItem> {
        val existedIds = this.items.map { it.mediaId }.toSet()
        return items.filter { !existedIds.contains(it.mediaId) }
    }
}

internal data class LoopRange(
    val start: Long = PlayerState.TIME_UNSET,
    val end: Long = PlayerState.TIME_UNSET,
) {
    val isStartSet: Boolean
        get() = start != PlayerState.TIME_UNSET

    val isEndSet: Boolean
        get() = end != PlayerState.TIME_UNSET

    val isSet: Boolean
        get() = isStartSet || isEndSet

    fun reset(start: Long?, end: Long?): LoopRange {
        // both are not changed
        if (start == null && end == null) return this.copy()

        val newStart: Long
        val newEnd: Long
        if (start == null) {
            // only end is changed
            // check start and end (start <= end) if needed (start is set, and end is to be set)
            if (this.start != PlayerState.TIME_UNSET && end != PlayerState.TIME_UNSET) {
                newStart = min(this.start, end!!)
                newEnd = max(this.start, end)
            } else {
                newStart = this.start
                newEnd = end!!
            }
        } else if (end == null) {
            // only start is changed
            if (start != PlayerState.TIME_UNSET && this.end != PlayerState.TIME_UNSET) {
                newStart = min(start, this.end)
                newEnd = max(start, this.end)
            } else {
                newStart = start
                newEnd = this.end
            }
        } else {
            if (start == PlayerState.TIME_UNSET || end == PlayerState.TIME_UNSET) {
                newStart = start
                newEnd = end
            } else {
                newStart = min(start, end)
                newEnd = max(start, end)
            }
        }

        return LoopRange(start = newStart, end = newEnd)
    }
}