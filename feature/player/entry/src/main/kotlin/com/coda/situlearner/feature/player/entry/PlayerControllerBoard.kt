package com.coda.situlearner.feature.player.entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.RepeatMode
import com.coda.situlearner.core.ui.util.UndefinedTimeText
import com.coda.situlearner.core.ui.util.asTimeText
import com.coda.situlearner.core.ui.widget.PlayNextButton
import com.coda.situlearner.core.ui.widget.PlaybackModeButton
import com.coda.situlearner.feature.player.entry.widgets.seekbar.SeekBar
import com.coda.situlearner.feature.player.entry.widgets.seekbar.SeekBarColors
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun PlayerControllerBoard(
    playerState: PlayerState,
    onNavigateToPlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlaying by playerState.isPlaying.collectAsStateWithLifecycle()
    val repeatMode by playerState.repeatMode.collectAsStateWithLifecycle()
    val loopInMs by playerState.loopInMs.collectAsStateWithLifecycle()
    val positionInMs by playerState.positionInMs.collectAsStateWithLifecycle()
    val durationInMs by playerState.durationInMs.collectAsStateWithLifecycle()
    val (loopStartInMs, loopEndInMs) = loopInMs

    val seekBarRange by remember(durationInMs, loopInMs) {
        derivedStateOf {
            durationInMs?.let {
                val start = loopStartInMs ?: 0L
                val end = loopEndInMs ?: it
                Pair(start, end)
            } ?: Pair(0L, 0L)
        }
    }

    PlayerControllerBoard(
        start = seekBarRange.first,
        end = seekBarRange.second,
        enabled = durationInMs != null,
        positionProvider = { positionInMs },
        onSeekToPosition = playerState::seekTo,
        isPlaying = isPlaying,
        repeatMode = repeatMode,
        hasLoopStart = loopStartInMs != null,
        hasLoopEnd = loopEndInMs != null,
        onSetRepeatMode = playerState::setRepeatMode,
        onPlayPrevious = playerState::playPrevious,
        onToggleShouldBePlaying = {
            if (it) playerState.play()
            else playerState.pause()
        },
        onPlayNext = playerState::playNext,
        onClickPlaylist = onNavigateToPlaylist,
        onToggleLoopStart = {
            if (it) playerState.setPlaybackLoop(positionInMs, null)
            else playerState.setPlaybackLoop(PlayerState.TIME_UNSET, null)
        },
        onToggleLoopEnd = {
            if (it) playerState.setPlaybackLoop(null, positionInMs)
            else playerState.setPlaybackLoop(null, PlayerState.TIME_UNSET)
        },
        modifier = modifier
    )
}

@Composable
private fun PlayerControllerBoard(
    // progress content param
    start: Long,
    end: Long,
    enabled: Boolean,
    positionProvider: () -> Long,
    onSeekToPosition: (Long) -> Unit,
    // action bar param
    isPlaying: Boolean,
    repeatMode: RepeatMode,
    hasLoopStart: Boolean,
    hasLoopEnd: Boolean,
    onSetRepeatMode: (RepeatMode) -> Unit,
    onPlayPrevious: () -> Unit,
    onToggleShouldBePlaying: (Boolean) -> Unit,
    onPlayNext: () -> Unit,
    onClickPlaylist: () -> Unit,
    onToggleLoopStart: (Boolean) -> Unit,
    onToggleLoopEnd: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ProgressContent(
            start = start,
            end = end,
            enabled = enabled,
            positionProvider = positionProvider,
            onSeekToPosition = onSeekToPosition,
        )
        ActionBar(
            isPlaying = isPlaying,
            repeatMode = repeatMode,
            hasLoopStart = hasLoopStart,
            hasLoopEnd = hasLoopEnd,
            onSetRepeatMode = onSetRepeatMode,
            onPlayPrevious = onPlayPrevious,
            onToggleShouldBePlaying = onToggleShouldBePlaying,
            onPlayNext = onPlayNext,
            onClickPlaylist = onClickPlaylist,
            onToggleLoopStart = onToggleLoopStart,
            onToggleLoopEnd = onToggleLoopEnd,
        )
    }
}

@Composable
private fun ProgressContent(
    start: Long,
    end: Long,
    enabled: Boolean,
    positionProvider: () -> Long,
    onSeekToPosition: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        var seekBarPosition by remember {
            mutableStateOf<Long?>(null)
        }

        SeekBar(
            start = start,
            end = end,
            valueProvider = { if (enabled) seekBarPosition ?: positionProvider() else start },
            onDragStart = { if (enabled) seekBarPosition = it },
            onDrag = { delta ->
                if (enabled) {
                    seekBarPosition = seekBarPosition?.plus(delta)?.coerceIn(start, end)
                }
            },
            onDragEnd = {
                if (enabled) {
                    seekBarPosition?.let(onSeekToPosition)
                    seekBarPosition = null
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SeekBarColors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
            ),
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = if (enabled) {
                    (seekBarPosition ?: positionProvider()).asTimeText()
                } else UndefinedTimeText,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = if (enabled) end.asTimeText() else UndefinedTimeText,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActionBar(
    isPlaying: Boolean,
    repeatMode: RepeatMode,
    hasLoopStart: Boolean,
    hasLoopEnd: Boolean,
    onSetRepeatMode: (RepeatMode) -> Unit,
    onPlayPrevious: () -> Unit,
    onToggleShouldBePlaying: (Boolean) -> Unit,
    onPlayNext: () -> Unit,
    onClickPlaylist: () -> Unit,
    onToggleLoopStart: (Boolean) -> Unit,
    onToggleLoopEnd: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            PlaybackModeButton(
                repeatMode = repeatMode,
                onSetRepeatMode = onSetRepeatMode
            )

            IconButton(
                onClick = onPlayPrevious
            ) {
                Icon(
                    painter = painterResource(coreR.drawable.skip_previous_24dp_000000_fill1_wght400_grad0_opsz24),
                    contentDescription = null
                )
            }

            Icon(
                painter = painterResource(
                    if (isPlaying) coreR.drawable.pause_24dp_000000_fill1_wght400_grad0_opsz24
                    else coreR.drawable.play_arrow_24dp_000000_fill1_wght400_grad0_opsz24
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clickable(
                        onClick = { onToggleShouldBePlaying(!isPlaying) },
                        interactionSource = null,
                        indication = ripple(
                            bounded = false,
                            radius = 40.dp
                        )
                    )
            )

            PlayNextButton(onPlayNext)

            IconButton(
                onClick = onClickPlaylist
            ) {
                Icon(
                    painter = painterResource(coreR.drawable.playlist_play_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            }
        }

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                onClick = { onToggleLoopStart(!hasLoopStart) }
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.start_24dp_000000_fill0_wght400_grad0_opsz24,
                    ),
                    contentDescription = null,
                    modifier = Modifier.alpha(
                        if (hasLoopStart) 1.0f else 0.5f
                    )
                )
            }

            IconButton(onClick = {}, enabled = false) {
                Icon(
                    painter = painterResource(R.drawable.spacer), contentDescription = null
                )
            }
            IconButton(onClick = {}, enabled = false) {
                Icon(
                    painter = painterResource(R.drawable.spacer), contentDescription = null
                )
            }
            IconButton(onClick = {}, enabled = false) {
                Icon(
                    painter = painterResource(R.drawable.spacer), contentDescription = null
                )
            }

            IconButton(
                onClick = { onToggleLoopEnd(!hasLoopEnd) }
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.start_24dp_000000_fill0_wght400_grad0_opsz24,
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .graphicsLayer(rotationZ = 180f)
                        .alpha(
                            if (hasLoopEnd) 1.0f else 0.5f
                        )
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewController() {
    PlayerControllerBoard(
        start = 0,
        end = 60000,
        enabled = true,
        positionProvider = { 30000L },
        onSeekToPosition = {},
        isPlaying = true,
        repeatMode = RepeatMode.All,
        hasLoopStart = true,
        hasLoopEnd = false,
        onSetRepeatMode = {},
        onPlayPrevious = { },
        onToggleShouldBePlaying = { },
        onPlayNext = { },
        onClickPlaylist = { },
        onToggleLoopStart = {},
        onToggleLoopEnd = {},
        modifier = Modifier
    )
}