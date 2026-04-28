package com.coda.situlearner.feature.player.fullscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.ui.util.UndefinedTimeText
import com.coda.situlearner.core.ui.util.asTimeText
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.SeekBar
import com.coda.situlearner.core.ui.widget.SeekBarColors
import com.coda.situlearner.feature.player.fullscreen.widget.AccelerationChip
import com.coda.situlearner.feature.player.fullscreen.widget.GestureContainer
import com.coda.situlearner.infra.player.PlayerState
import kotlinx.coroutines.delay

@Composable
internal fun ControllerBoard(
    playerState: PlayerState,
    onNoAction: () -> Unit,
    onDoubleTapMediaBoardLeft: () -> Unit,
    onDoubleTapMediaBoardRight: () -> Unit,
    onDismissFullscreen: () -> Unit,
) {
    var hasAction by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(hasAction) {
        if (!hasAction) {
            delay(3000)
            onNoAction()
        }
    }

    val isPlaying by playerState.isPlaying.collectAsStateWithLifecycle()
    val durationInMs by playerState.durationInMs.collectAsStateWithLifecycle()
    val playlist by playerState.playlist.collectAsStateWithLifecycle()
    val positionInMs by playerState.positionInMs.collectAsStateWithLifecycle()
    val loopInMs by playerState.loopInMs.collectAsStateWithLifecycle()

    val currentItem = playlist.currentItem ?: return
    val (loopStartInMs, loopEndInMs) = loopInMs

    // NOTE: basically the same as PlayerControllerBoard
    // in feature/player/entry
    val seekBarRange by remember(durationInMs, loopInMs) {
        derivedStateOf {
            durationInMs?.let {
                val start = loopStartInMs ?: 0L
                val end = loopEndInMs ?: it
                Pair(start, end)
            } ?: Pair(0L, 0L)
        }
    }

    var showAccelerationChip by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TitleTopBar(
            currentItem = currentItem,
            onDismiss = onDismissFullscreen,
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )

        GestureContainer(
            onTap = onNoAction,
            onDoubleTapLeft = onDoubleTapMediaBoardLeft,
            onDoubleTapRight = onDoubleTapMediaBoardRight,
            onDoubleTapCenter = {
                if (isPlaying) playerState.pause()
                else playerState.play()
            },
            onLongPressStart = {
                if (isPlaying) {
                    hasAction = true
                    showAccelerationChip = true
                    playerState.setPlaybackSpeed(2.0f)
                }
            },
            onLongPressEnd = {
                if (showAccelerationChip) {
                    hasAction = false
                    showAccelerationChip = false
                    playerState.setPlaybackSpeed(1.0f)
                }
            },
            modifier = Modifier.weight(1f)
        )

        ProgressContent(
            start = seekBarRange.first,
            end = seekBarRange.second,
            enabled = durationInMs != null,
            positionProvider = { positionInMs },
            onDragStart = { hasAction = true },
            onDragEnd = {
                hasAction = false
                playerState.seekTo(it)
            },
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f),
                        )
                    )
                )
                // NOTE: this is a workaround to force Compose to collect pointer events.
                // Otherwise, the drag gesture will be intercepted by video area and not work on
                // progress bar (even if we've placed controller board above the media board).
                // Maybe a bug in playerView in media3.
                // This could be reproduced as:
                //      Box(modifier = Modifier.fillMaxSize()) {
                //          playerState.VideoOutput(modifier = Modifier.fillMaxSize())
                //          SeekBar(...)
                //      }
                .pointerInput(Unit) {
                    detectTapGestures { }
                }
        )
    }

    if (showAccelerationChip) AccelerationChip()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TitleTopBar(
    currentItem: PlaylistItem?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = currentItem?.name ?: "") },
        navigationIcon = {
            BackButton(onBack = onDismiss)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
        )
    )
}

@Composable
private fun ProgressContent(
    start: Long,
    end: Long,
    enabled: Boolean,
    positionProvider: () -> Long,
    onDragEnd: (Long) -> Unit,
    onDragStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        var seekBarPosition by remember {
            mutableStateOf<Long?>(null)
        }

        val currentText = if (enabled) {
            (seekBarPosition ?: positionProvider()).asTimeText()
        } else UndefinedTimeText
        val endText = if (enabled) end.asTimeText() else UndefinedTimeText

        Text(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            text = "$currentText / $endText",
            fontSize = 12.sp,
            color = Color.White,
        )

        SeekBar(
            start = start,
            end = end,
            valueProvider = { if (enabled) seekBarPosition ?: positionProvider() else start },
            onDragStart = {
                if (enabled) {
                    onDragStart()
                    seekBarPosition = it
                }
            },
            onDrag = { delta ->
                if (enabled) {
                    seekBarPosition = seekBarPosition?.plus(delta)?.coerceIn(start, end)
                }
            },
            onDragEnd = {
                if (enabled) {
                    seekBarPosition?.let(onDragEnd)
                    seekBarPosition = null
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SeekBarColors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = Color.Gray,
            )
        )

        Spacer(Modifier.height(48.dp))
    }
}