package com.coda.situlearner.feature.player.fullscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.feature.player.fullscreen.util.KeepScreenOn
import com.coda.situlearner.feature.player.fullscreen.widget.AccelerationChip
import com.coda.situlearner.feature.player.fullscreen.widget.GestureContainer
import com.coda.situlearner.infra.player.PlayerState

@Composable
internal fun MediaBoard(
    playerState: PlayerState,
    onTapMediaBoard: () -> Unit,
    onDoubleTapMediaBoardLeft: () -> Unit,
    onDoubleTapMediaBoardRight: () -> Unit,
) {
    val isPlaying by playerState.isPlaying.collectAsStateWithLifecycle()
    val playlist by playerState.playlist.collectAsStateWithLifecycle()

    val currentItem = playlist.currentItem ?: return

    var showAccelerationChip by remember { mutableStateOf(false) }

    if (isPlaying) KeepScreenOn(LocalView.current)

    Box(modifier = Modifier.fillMaxSize()) {
        MediaOutput(
            currentItem,
            playerState,
            modifier = Modifier.fillMaxSize()
        )

        GestureContainer(
            onTap = onTapMediaBoard,
            onDoubleTapLeft = onDoubleTapMediaBoardLeft,
            onDoubleTapRight = onDoubleTapMediaBoardRight,
            onDoubleTapCenter = {
                if (isPlaying) playerState.pause()
                else playerState.play()
            },
            onLongPressStart = {
                if (isPlaying) {
                    showAccelerationChip = true
                    playerState.setPlaybackSpeed(2.0f)
                }
            },
            onLongPressEnd = {
                if (showAccelerationChip) {
                    showAccelerationChip = false
                    playerState.setPlaybackSpeed(1.0f)
                }
            }
        )
    }

    if (showAccelerationChip) AccelerationChip()
}

@Composable
private fun MediaOutput(
    currentItem: PlaylistItem,
    playerState: PlayerState,
    modifier: Modifier = Modifier
) {
    when (currentItem.mediaType) {
        MediaType.Audio -> {
            Box(modifier = modifier) {
                AsyncMediaImage(
                    model = currentItem.thumbnailUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(radius = 50.dp)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop
                )

                AsyncMediaImage(
                    model = currentItem.thumbnailUrl,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                )
            }
        }

        MediaType.Video -> {
            Box(modifier = modifier.background(Color.Black)) {
                playerState.VideoOutput(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}