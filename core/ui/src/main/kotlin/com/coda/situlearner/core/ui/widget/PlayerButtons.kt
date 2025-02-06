package com.coda.situlearner.core.ui.widget

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.coda.situlearner.core.model.data.RepeatMode
import com.coda.situlearner.core.ui.R

@Composable
fun PlayOrPauseButton(
    isPlaying: Boolean,
    onToggleShouldBePlaying: (Boolean) -> Unit,
) {
    IconButton(
        onClick = {
            onToggleShouldBePlaying(!isPlaying)
        }
    ) {
        Icon(
            painter = painterResource(
                if (isPlaying) R.drawable.pause_24dp_000000_fill1_wght400_grad0_opsz24
                else R.drawable.play_arrow_24dp_000000_fill1_wght400_grad0_opsz24
            ),
            contentDescription = null
        )
    }
}

@Composable
fun PlayNextButton(
    onPlayNext: () -> Unit,
) {
    IconButton(
        onClick = onPlayNext
    ) {
        Icon(
            painter = painterResource(R.drawable.skip_next_24dp_000000_fill1_wght400_grad0_opsz24),
            contentDescription = null
        )
    }
}

@Composable
fun PlaybackModeButton(
    repeatMode: RepeatMode,
    onSetRepeatMode: (RepeatMode) -> Unit
) {
    IconButton(
        onClick = { onSetRepeatMode(repeatMode.nextMode()) }
    ) {
        Icon(
            painter = painterResource(
                when (repeatMode) {
                    RepeatMode.One -> R.drawable.repeat_one_24dp_000000_fill0_wght400_grad0_opsz24
                    RepeatMode.All -> R.drawable.repeat_24dp_000000_fill0_wght400_grad0_opsz24
                }
            ),
            contentDescription = null
        )
    }
}

private fun RepeatMode.nextMode() = when (this) {
    RepeatMode.One -> RepeatMode.All
    RepeatMode.All -> RepeatMode.One
}