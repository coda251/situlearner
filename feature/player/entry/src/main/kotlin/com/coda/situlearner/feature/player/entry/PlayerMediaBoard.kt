package com.coda.situlearner.feature.player.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.infra.player.PlayerState

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
                    onBack = onBack
                )
            }

            MediaType.Video -> {
                PlayerVideoBoard(playerState, modifier)
            }
        }
    }
}

@Composable
private fun PlayerVideoBoard(
    playerState: PlayerState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black)
            .fillMaxWidth()
            .aspectRatio(16 / 9f)

    ) {
        playerState.VideoOutput(modifier = Modifier)
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
        TopAppBar(
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            navigationIcon = {
                BackButton(onBack)
            }
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