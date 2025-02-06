package com.coda.situlearner.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.media.session.MediaSession
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.coda.situlearner.MainActivity
import com.coda.situlearner.R
import com.coda.situlearner.core.model.data.Playlist
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.infra.player.PlayerState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.coda.situlearner.core.ui.R as coreR

class PlayerNotification(
    private val playerState: PlayerState,
    private val service: LifecycleService,
    private val mediaSession: MediaSession,
) {
    companion object {
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "SituLearner Player Notification"
    }

    private var shouldStartForeGround = true
    private var hasNotification = false

    private val notificationManager: NotificationManager? = service.getSystemService()

    init {
        notificationManager?.apply {
            if (getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
                createNotificationChannel(
                    NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        "Playing item",
                        NotificationManager.IMPORTANCE_LOW,
                    )
                )
            }
        }
    }

    private fun build(
        context: Context,
        item: PlaylistItem?,
        isPlaying: Boolean,
    ): Notification? {
        if (item == null) return null

        val builder = Notification.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            setContentTitle(item.name)
            setContentText(item.collectionName)
            setSmallIcon(R.mipmap.ic_launcher)
            setShowWhen(false)
            setVisibility(Notification.VISIBILITY_PUBLIC)
            setContentIntent(
                PendingIntent.getActivity(
                    context,
                    NOTIFICATION_ID,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(
                        context,
                        coreR.drawable.skip_previous_24dp_000000_fill1_wght400_grad0_opsz24,
                    ),
                    Action.previous.value,
                    Action.previous.getPendingIntent(context, 100)
                ).build()
            )
            addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(
                        context,
                        if (isPlaying) coreR.drawable.pause_24dp_000000_fill1_wght400_grad0_opsz24
                        else coreR.drawable.play_arrow_24dp_000000_fill1_wght400_grad0_opsz24,
                    ),
                    if (isPlaying) Action.pause.value
                    else Action.play.value,
                    if (isPlaying) Action.pause.getPendingIntent(context, 100)
                    else Action.play.getPendingIntent(context, 100)
                ).build()
            )
            addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(
                        context,
                        coreR.drawable.skip_next_24dp_000000_fill1_wght400_grad0_opsz24
                    ),
                    Action.next.value,
                    Action.next.getPendingIntent(context, 100)
                ).build()
            )

            style = Notification.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSession.sessionToken)
        }

        return builder.build()
    }


    fun update() {
        service.apply {
            lifecycleScope.launch {
                combine(playerState.isPlaying, playerState.playlist) { t -> t }
                    .collect { t ->
                        val isPlaying = t[0] as Boolean
                        val item = (t[1] as Playlist).currentItem

                        val notification = build(
                            context = this@apply,
                            item = item,
                            isPlaying = isPlaying
                        )

                        notification?.let {
                            if (!hasNotification) {
                                if (shouldStartForeGround) {
                                    startForegroundService(
                                        Intent(
                                            this@apply,
                                            PlayerService::class.java
                                        )
                                    )
                                    shouldStartForeGround = false
                                }

                                hasNotification = true
                                startForeground(NOTIFICATION_ID, it)
                            } else {
                                notificationManager?.notify(NOTIFICATION_ID, notification)
                            }
                        } ?: let {
                            hasNotification = false
                            stopForeground(Service.STOP_FOREGROUND_REMOVE)
                            shouldStartForeGround = true
                            notificationManager?.cancel(NOTIFICATION_ID)
                        }
                    }
            }
        }
    }
}

@JvmInline
private value class Action(val value: String) {

    fun getPendingIntent(
        context: Context,
        requestCode: Int,
    ): PendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        Intent(value).setPackage(context.packageName),
        PendingIntent.FLAG_IMMUTABLE
    )

    companion object {
        val pause = Action("PlayerService.pause")
        val play = Action("PlayerService.play")
        val next = Action("PlayerService.next")
        val previous = Action("PlayerService.previous")
    }
}

class NotificationReceiver(
    private val playerState: PlayerState,
) : BroadcastReceiver() {

    companion object {
        fun buildFilter() = IntentFilter().apply {
            addAction(Action.play.value)
            addAction(Action.pause.value)
            addAction(Action.previous.value)
            addAction(Action.next.value)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when (it.action) {
                Action.play.value -> playerState.play()
                Action.pause.value -> playerState.pause()
                Action.next.value -> playerState.playNext()
                Action.previous.value -> playerState.playPrevious()
                else -> {}
            }
        }
    }
}