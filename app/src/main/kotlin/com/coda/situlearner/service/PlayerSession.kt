package com.coda.situlearner.service

import android.media.session.MediaSession
import androidx.lifecycle.LifecycleService

class PlayerSession(
    service: LifecycleService
) {
    val mediaSession = MediaSession(service, "PlayerSession")

    // TODO: send playback info to system
    init {
        mediaSession.isActive = true
    }

    fun onDestroy() {
        mediaSession.isActive = false
        mediaSession.release()
    }
}