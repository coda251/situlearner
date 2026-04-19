package com.coda.situlearner.service

import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import androidx.lifecycle.LifecycleService
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.infra.player.PlayerState

class PlayerSession(
    service: LifecycleService,
    private val playerState: PlayerState
) {
    val mediaSession = MediaSession(service, "PlayerSession")

    private val stateBuilder = PlaybackState.Builder()
        .setActions(
            PlaybackState.ACTION_PLAY
                    or PlaybackState.ACTION_PAUSE
                    or PlaybackState.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackState.ACTION_SKIP_TO_NEXT
                    or PlaybackState.ACTION_SEEK_TO
        )

    init {
        mediaSession.isActive = true
        initCallback()
    }

    fun onDestroy() {
        mediaSession.isActive = false
        mediaSession.release()
    }

    fun updateMetadata(item: PlaylistItem?, bitmap: Bitmap, duration: Long) {
        val metadata = MediaMetadata.Builder().apply {
            putString(MediaMetadata.METADATA_KEY_TITLE, item?.name)
            putString(MediaMetadata.METADATA_KEY_ARTIST, item?.collectionName)
            putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap)
            putLong(MediaMetadata.METADATA_KEY_DURATION, duration)
        }.build()
        mediaSession.setMetadata(metadata)
    }

    fun updatePlaybackState(isPlaying: Boolean, positionInMs: Long) {
        stateBuilder.setState(
            if (isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED,
            positionInMs,
            1f
        )
        mediaSession.setPlaybackState(stateBuilder.build())
    }

    private fun initCallback() {
        mediaSession.setCallback(object : MediaSession.Callback() {
            override fun onPlay() = playerState.play()
            override fun onPause() = playerState.pause()
            override fun onSkipToPrevious() = playerState.playPrevious()
            override fun onSkipToNext() = playerState.playNext()
            override fun onSeekTo(pos: Long) = playerState.seekTo(pos)
        })
    }
}