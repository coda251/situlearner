package com.coda.situlearner.feature.player.fullscreen

import android.content.ClipData
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.SubtitleDisplayMode
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.core.ui.widget.SubtitleListItem
import com.coda.situlearner.core.ui.widget.SubtitleTextDefault
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerState.Companion.TIME_UNSET

@Composable
internal fun SubtitleBoard(
    playerState: PlayerState,
    subtitle: Subtitle,
    showTargetText: Boolean,
    subtitleDisplayMode: SubtitleDisplayMode,
    onShowTargetText: (Subtitle) -> Unit,
    onClickToken: (Token, Subtitle) -> Unit,
    modifier: Modifier = Modifier
) {
    val loop by playerState.loopInMs.collectAsStateWithLifecycle()

    val clipboard = LocalClipboard.current
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        SubtitleListItem(
            sourceText = subtitle.sourceText,
            targetText = subtitle.targetText,
            tokens = subtitle.tokens,
            isActive = false,
            isInClip = false,
            showTargetText = showTargetText,
            // since bottom sheet will hide subtitle in ui,
            // we do not highlight active token here
            activeTokenStartIndex = -1,
            onClickToken = { token ->
                onClickToken(
                    token,
                    subtitle,
                )
            },
            onClickStartBox = {
                playerState.seekTo(subtitle.startTimeInMs)
            },
            onClickEndBox = {
                when (subtitleDisplayMode) {
                    SubtitleDisplayMode.All -> playerState.seekTo(subtitle.startTimeInMs)
                    SubtitleDisplayMode.OnlySourceText -> {
                        onShowTargetText(subtitle)
                    }
                }
            },
            onDoubleClickBox = {
                if (loop.first == subtitle.startTimeInMs && loop.second == subtitle.endTimeInMs) {
                    playerState.setPlaybackLoop(
                        TIME_UNSET,
                        TIME_UNSET
                    )
                } else {
                    playerState.setPlaybackLoop(subtitle.startTimeInMs, subtitle.endTimeInMs)
                }
            },
            onLongClickBox = {
                clipboard.nativeClipboard.setPrimaryClip(
                    ClipData.newPlainText(
                        "text",
                        subtitle.sourceText
                    )
                )
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
            },
            subtitleTextColors = SubtitleTextDefault.subtitleTextColors(
                defaultSourceTextColor = Color.White,
                defaultTargetTextColor = Color.White,
                containerColor = Color.Black.copy(alpha = 0.3f)
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 4.dp)
        )
    }
}