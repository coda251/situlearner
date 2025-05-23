package com.coda.situlearner.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Binder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.data.repository.PlayerStateRepository
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.PlayerStateData
import com.coda.situlearner.core.model.data.Playlist
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.model.data.PlaylistType
import com.coda.situlearner.core.model.data.RepeatMode
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.ui.theme.themeColorFromImage
import com.coda.situlearner.infra.player.PlayerEngine
import com.coda.situlearner.infra.player.PlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class PlayerService : LifecycleService(), KoinComponent, PlayerState {

    private val playerEngine: PlayerEngine by inject { parametersOf(lifecycleScope) }
    private val playerStateRepository: PlayerStateRepository by inject()
    private val userPreferenceRepository: UserPreferenceRepository by inject()

    private var currentPlaylistType = PlaylistType.Persistent
    private var restorePlayerStateJob: Job? = null
    private var savePlayerStateJob: Job? = null

    private val notificationReceiver = NotificationReceiver(this)

    private lateinit var playerSession: PlayerSession

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        playerEngine.onCreate()

        restorePlayerStateJob = startRestorePlayerStateJob()
        restorePlayerStateJob?.invokeOnCompletion {
            savePlayerStateJob = startSavePlayerStateJob()
        }
        updateThumbnailThemeColorJob()

        playerSession = PlayerSession(this)

        registerReceiver(
            notificationReceiver,
            NotificationReceiver.buildFilter()
        )
        PlayerNotification(this, this, playerSession.mediaSession).update()
    }

    override fun onBind(intent: Intent): PlayerBinder {
        super.onBind(intent)
        return PlayerBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerEngine.onDestroy()
        playerSession.onDestroy()
        unregisterReceiver(notificationReceiver)
    }

    inner class PlayerBinder : Binder() {
        val state: PlayerState
            get() = this@PlayerService
    }

    private fun startRestorePlayerStateJob() = lifecycleScope.launch {
        playerStateRepository.playerStateData.firstOrNull()?.let {
            withContext(Dispatchers.Main) {
                playerEngine.restoreFrom(it)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun startSavePlayerStateJob() = lifecycleScope.launch {
        launch {
            combine(
                playerEngine.repeatMode,
                playerEngine.playlist
            ) { mode, list -> mode to list }.collectLatest {
                playerStateRepository.setRepeatMode(it.first)
                playerStateRepository.setPlaylist(it.second)
            }
        }

        launch {
            playerEngine.positionInMs.sample(2000).collectLatest {
                playerStateRepository.setPositionInMs(it)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun updateThumbnailThemeColorJob() {
        val provider = ThumbnailColorFlowProvider(
            context = this,
            defaultColor = Color(AppConfig.DEFAULT_THEME_COLOR)
        )
        lifecycleScope.launch {
            userPreferenceRepository.userPreference.filter {
                it.themeColorMode == ThemeColorMode.DynamicWithThumbnail
            }.flatMapLatest {
                provider.provideColorFlow(this@PlayerService)
                    .distinctUntilChanged()
                    // drop the initial empty list value
                    .drop(1)
            }.collectLatest {
                // need to convert to argb first
                userPreferenceRepository.setThumbnailThemeColor(it.toArgb().toLong())
            }
        }
    }

    override val repeatMode: StateFlow<RepeatMode>
        get() = playerEngine.repeatMode
    override val positionInMs: StateFlow<Long>
        get() = playerEngine.positionInMs
    override val durationInMs: StateFlow<Long?>
        get() = playerEngine.durationInMs
    override val isPlaying: StateFlow<Boolean>
        get() = playerEngine.isPlaying
    override val playWhenReady: StateFlow<Boolean>
        get() = playerEngine.playWhenReady
    override val loopInMs: StateFlow<Pair<Long?, Long?>>
        get() = playerEngine.loopInMs

    override fun play() {
        playerEngine.play()
    }

    override fun pause() {
        playerEngine.pause()
    }

    override fun playNext() {
        playerEngine.playNext()
    }

    override fun playPrevious() {
        playerEngine.playPrevious()
    }

    override fun seekTo(position: Long) {
        playerEngine.seekTo(position)
    }

    override fun seekToItem(index: Int) {
        playerEngine.seekToItem(index)
    }

    override fun seekToItem(item: PlaylistItem) {
        playerEngine.seekToItem(item)
    }

    override fun setRepeatMode(mode: RepeatMode) {
        playerEngine.setRepeatMode(mode)
    }

    override fun setPlaybackSpeed(speed: Float) {
        playerEngine.setPlaybackSpeed(speed)
    }

    override fun setPlaybackLoop(start: Long?, end: Long?) {
        playerEngine.setPlaybackLoop(start, end)
    }

    override fun setRepeatNumber(number: Int) {
        playerEngine.setRepeatNumber(number)
    }

    override val playlist: StateFlow<Playlist>
        get() = playerEngine.playlist
    override val playlistType: StateFlow<PlaylistType>
        get() = playerEngine.playlistType

    override fun addItems(items: List<PlaylistItem>, startItem: PlaylistItem?) {
        playerEngine.addItems(items, startItem)
    }

    override fun setItems(items: List<PlaylistItem>) {
        playerEngine.setItems(items)
    }

    override fun removeItem(index: Int) {
        playerEngine.removeItem(index)
    }

    override fun clear() {
        playerEngine.clear()
    }

    override fun swapItems(fromIndex: Int, toIndex: Int) {
        playerEngine.swapItems(fromIndex, toIndex)
    }

    override fun shufflePlaylist() {
        playerEngine.shufflePlaylist()
    }

    override fun restoreFrom(data: PlayerStateData) {
        playerEngine.restoreFrom(data)
    }

    override fun switchPlaylistType(type: PlaylistType) {
        if (type != currentPlaylistType) {
            currentPlaylistType = type
            playerEngine.switchPlaylistType(type)
            when (type) {
                PlaylistType.Persistent -> {
                    playerEngine.pause()
                    playerEngine.clear()

                    restorePlayerStateJob = startRestorePlayerStateJob()
                    restorePlayerStateJob?.invokeOnCompletion {
                        savePlayerStateJob = startSavePlayerStateJob()
                    }
                }

                PlaylistType.Temporary -> {
                    // first cancel jobs
                    restorePlayerStateJob?.cancel()
                    restorePlayerStateJob = null
                    savePlayerStateJob?.cancel()
                    savePlayerStateJob = null

                    // then clear playlist to avoid the empty list being written to disk
                    playerEngine.pause()
                    playerEngine.clear()
                }
            }
        }
    }

    @Composable
    override fun VideoOutput(modifier: Modifier) {
        playerEngine.VideoOutput(modifier)
    }
}

private class ThumbnailColorFlowProvider(
    private val context: Context,
    private val defaultColor: Color,
) {
    private val cachedUrlToColor: MutableMap<String, Color> = mutableMapOf()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun provideColorFlow(state: PlayerState): Flow<Color> =
        state.playlistType.flatMapLatest {
            when (it) {
                PlaylistType.Persistent -> {
                    state.playlist.map { playlist ->
                        resolveColorFromPlaylist(playlist)
                    }
                }

                // when playlistType goes to temporary, we do not emit value
                // to avoid color change animation
                PlaylistType.Temporary -> emptyFlow()
            }
        }

    private suspend fun resolveColorFromPlaylist(playlist: Playlist): Color {
        val url = playlist.currentItem?.thumbnailUrl ?: return defaultColor
        val thumbnailColor = cachedUrlToColor[url] ?: themeColorFromImage(url, context)
        if (thumbnailColor != null) {
            cachedUrlToColor[url] = thumbnailColor
            return thumbnailColor
        } else return defaultColor
    }
}