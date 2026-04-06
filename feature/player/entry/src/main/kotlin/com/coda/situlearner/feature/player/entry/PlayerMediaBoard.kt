package com.coda.situlearner.feature.player.entry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.infra.player.PlayerState
import kotlinx.coroutines.delay

@Composable
internal fun PlayerMediaBoard(
    playerState: PlayerState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playlist by playerState.playlist.collectAsStateWithLifecycle()

    playlist.currentItem?.let {
        when (it.mediaType) {
            MediaType.Audio -> {
                PlayerAudioBoard(
                    item = it,
                    modifier = modifier,
                    onBack = onBack,
                )
            }

            MediaType.Video -> {
                PlayerVideoBoard(
                    playerState,
                    onBack = onBack,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun PlayerVideoBoard(
    playerState: PlayerState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showController by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(showController) {
        if (showController) {
            delay(3000)
            showController = false
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showController = !showController
            }
    ) {
        playerState.VideoOutput(modifier = Modifier)

        AnimatedVisibility(
            visible = showController,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.White) {
                NavigationControllerTopBar(
                    onBack = onBack,
                    modifier = Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerAudioBoard(
    item: PlaylistItem,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        NavigationControllerTopBar(
            onBack = onBack,
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncMediaImage(
                model = item.thumbnailUrl,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun NavigationControllerTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .then(modifier),
    ) {
        BackButton(
            onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp)
        )
    }
}