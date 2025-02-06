package com.coda.situlearner.feature.player.entry

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.infra.player.PlayerState

@Composable
internal fun PlayerItemInfoBoard(
    playerState: PlayerState,
    modifier: Modifier = Modifier
) {
    val playlist by playerState.playlist.collectAsStateWithLifecycle()

    playlist.currentItem?.let {
        PlayerItemInfoBoard(currentItem = it, modifier = modifier)
    }
}

@Composable
private fun PlayerItemInfoBoard(
    currentItem: PlaylistItem,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = currentItem.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = currentItem.collectionName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },

        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}