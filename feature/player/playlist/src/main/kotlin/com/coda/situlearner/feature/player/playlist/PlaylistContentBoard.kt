package com.coda.situlearner.feature.player.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.model.data.Playlist
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.model.data.RepeatMode
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.PlaybackModeButton
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun PlaylistContentBoard(
    playlist: Playlist,
    isPlaying: Boolean,
    repeatMode: RepeatMode,
    onSwapItems: (Int, Int) -> Unit,
    onSeekToItem: (Int) -> Unit,
    onToggleShouldBePlaying: (Boolean) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onClearItems: () -> Unit,
    onSetRepeatMode: (RepeatMode) -> Unit,
    onShufflePlaylist: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var openClearDialog by rememberSaveable { mutableStateOf(false) }

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = playlist.currentIndex
    )
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onSwapItems(from.index, to.index)
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onBack() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PlaybackModeButton(repeatMode) { onSetRepeatMode(it) }
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_downward_24dp_000000_fill0_wght400_grad0_opsz24),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            openClearDialog = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(coreR.drawable.delete_24dp_000000_fill0_wght400_grad0_opsz24),
                            contentDescription = null
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onShufflePlaylist) {
                Icon(
                    painter = painterResource(R.drawable.shuffle_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            }
        },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp),
                state = lazyListState,
            ) {
                itemsIndexed(
                    items = playlist,
                    key = { _, it -> it.id }
                ) { index, it ->
                    val isCurrentItem = index == playlist.currentIndex
                    ReorderableItem(state = reorderableLazyListState, key = it.id) { _ ->
                        PlaylistItemView(
                            modifier = Modifier.animateItem(),
                            item = it,
                            isCurrent = isCurrentItem,
                            scope = this,
                            onClick = {
                                if (!isCurrentItem) {
                                    onSeekToItem(index)
                                    onToggleShouldBePlaying(true)
                                } else {
                                    onToggleShouldBePlaying(!isPlaying)
                                }
                            },
                            onRemove = {
                                onRemoveItem(index)
                            },
                        )
                    }
                }
            }
        }
    }

    if (openClearDialog) {
        ClearPlaylistDialog(
            onDismiss = {
                openClearDialog = false
            },
            onConfirm = {
                onClearItems()
                openClearDialog = false
            }
        )
    }
}

@Composable
private fun ClearPlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(coreR.string.core_ui_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(coreR.string.core_ui_cancel))
            }
        },
        title = {
            Text(text = stringResource(R.string.player_playlist_screen_clear_playlist_title))
        },
        text = {
            Text(text = stringResource(R.string.player_playlist_screen_clear_playlist_text))
        },
        modifier = modifier
    )
}


@Composable
private fun PlaylistItemView(
    item: PlaylistItem,
    isCurrent: Boolean,
    scope: ReorderableCollectionItemScope,
    onRemove: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(positionalThreshold = { it * .3f })

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            onRemove()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.26f))
            )
        },
        modifier = modifier.clickable(onClick = onClick)
    ) {
        ListItem(
            leadingContent = {
                AsyncMediaImage(
                    model = item.thumbnailUrl, modifier = Modifier
                        .size(56.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                )
            },
            headlineContent = {
                Text(
                    text = item.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isCurrent) FontWeight.Bold
                    else FontWeight.Normal
                )
            },
            supportingContent = {
                Text(
                    text = item.collectionName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isCurrent) FontWeight.Bold
                    else FontWeight.Normal
                )
            },
            trailingContent = {
                IconButton(
                    onClick = {},
                    modifier = with(scope) {
                        Modifier.draggableHandle()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.drag_handle_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = null
                    )
                }
            },
        )
    }
}

@Preview
@Composable
private fun ClearPlaylistDialogPreview() {
    ClearPlaylistDialog(onConfirm = {}, onDismiss = {})
}

@Preview
@Composable
private fun PlaylistContentBoardPreview() {
    val playlist = Playlist(
        items = listOf(
            PlaylistItem(
                id = "0",
                name = "Example S01E01",
                collectionName = "Example",
                mediaUrl = "",
                subtitleUrl = "file:///storage/emulated/0/Download/fake.stk",
                mediaType = MediaType.Video,
                thumbnailUrl = null,
            ),
            PlaylistItem(
                id = "1",
                name = "",
                collectionName = "Example",
                mediaUrl = "",
                subtitleUrl = null,
                mediaType = MediaType.Audio,
                thumbnailUrl = null,
            ),
            PlaylistItem(
                id = "2",
                name = "A file with really really really really really really long name",
                collectionName = "A collection with really really really really really really long name",
                mediaUrl = "",
                subtitleUrl = null,
                mediaType = MediaType.Audio,
                thumbnailUrl = null,
            )
        ),
        currentIndex = 2
    )

    PlaylistContentBoard(
        playlist = playlist,
        isPlaying = true,
        repeatMode = RepeatMode.One,
        onSwapItems = { _, _ -> },
        onSeekToItem = {},
        onToggleShouldBePlaying = {},
        onRemoveItem = {},
        onClearItems = {},
        onSetRepeatMode = {},
        onShufflePlaylist = {},
        onBack = {}
    )
}