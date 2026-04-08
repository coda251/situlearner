package com.coda.situlearner.feature.home.media.collection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.core.model.data.mapper.asPlaylist
import com.coda.situlearner.core.model.data.mapper.asPlaylistItem
import com.coda.situlearner.core.ui.util.UndefinedTimeText
import com.coda.situlearner.core.ui.util.asTimeText
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.NonEmptyTextInputDialog
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun MediaCollectionScreen(
    onBack: () -> Unit,
    onNavigateToExplore: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MediaCollectionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()

    MediaCollectionScreen(
        uiState = uiState,
        playerState = playerState,
        actionState = actionState,
        onBack = onBack,
        onNavigateToExplore = onNavigateToExplore,
        onDeleteCollection = viewModel::deleteMediaCollection,
        onRenameCollection = viewModel::setMediaCollectionName,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaCollectionScreen(
    uiState: MediaCollectionUiState,
    playerState: PlayerState,
    actionState: ActionState,
    onBack: () -> Unit,
    onNavigateToExplore: (String) -> Unit,
    onDeleteCollection: () -> Unit,
    onRenameCollection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(actionState) {
        // it's quick for deletion so we don't add block ui to avoid flash
        if (actionState is ActionState.Deleted) {
            onBack()
        }
    }

    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (uiState) {
                        is MediaCollectionUiState.Success -> Text(uiState.collection.name)
                        else -> {}
                    }
                },
                navigationIcon = {
                    BackButton(onBack = onBack)
                },
                actions = {
                    when (uiState) {
                        is MediaCollectionUiState.Success -> {
                            MoreActionMenu(
                                onSetPlaylist = {
                                    playerState.setItems(
                                        uiState.asMediaCollectionWithFiles().asPlaylist()
                                    )
                                    playerState.play()
                                },
                                onAddMediaFiles = {
                                    onNavigateToExplore(uiState.collection.url)
                                },
                                onDeleteCollection = {
                                    showDeleteDialog = true
                                },
                                onRenameCollection = {
                                    showRenameDialog = true
                                }
                            )
                        }

                        else -> {}
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                MediaCollectionUiState.Error -> {}
                MediaCollectionUiState.Loading -> {}
                is MediaCollectionUiState.Success -> {
                    MediaCollectionContentBoard(
                        files = uiState.files,
                        onClickMediaFile = {
                            (uiState.collection to it).asPlaylistItem().apply {
                                playerState.addItems(listOf(this), this)
                                playerState.play()
                            }
                        }
                    )
                }
            }
        }
    }

    if (showRenameDialog && uiState is MediaCollectionUiState.Success) {
        NonEmptyTextInputDialog(
            text = uiState.collection.name,
            onDismiss = {
                showRenameDialog = false
            },
            onConfirm = {
                onRenameCollection(it)
                showRenameDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        DeleteCollectionDialog(
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                onDeleteCollection()
                showDeleteDialog = false
            }
        )
    }
}

@Composable
private fun MediaCollectionContentBoard(
    files: List<MediaFile>,
    onClickMediaFile: (MediaFile) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(
            items = files,
            key = { _, it -> it.id }
        ) { index, it ->
            MediaFileItem(
                index = index,
                file = it,
                onClick = { onClickMediaFile(it) }
            )
        }
    }
}

@Composable
private fun MoreActionMenu(
    onSetPlaylist: () -> Unit,
    onAddMediaFiles: () -> Unit,
    onRenameCollection: () -> Unit,
    onDeleteCollection: () -> Unit,
) {
    var showFileListMenu by remember {
        mutableStateOf(false)
    }

    IconButton(onClick = { showFileListMenu = true }) {
        Icon(
            painter = painterResource(coreR.drawable.more_vert_24dp_000000_fill0_wght400_grad0_opsz24),
            contentDescription = null
        )
    }

    DropdownMenu(
        expanded = showFileListMenu,
        onDismissRequest = { showFileListMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.home_media_collection_screen_set_playlist)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(coreR.drawable.playlist_play_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            },
            onClick = {
                onSetPlaylist()
                showFileListMenu = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.home_media_collection_screen_add_media_files)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(coreR.drawable.add_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            },
            onClick = {
                onAddMediaFiles()
                showFileListMenu = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.home_media_collection_screen_rename_collection)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(coreR.drawable.edit_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            },
            onClick = {
                onRenameCollection()
                showFileListMenu = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = coreR.string.core_ui_delete)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(coreR.drawable.delete_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            },
            onClick = {
                onDeleteCollection()
                showFileListMenu = false
            }
        )
    }
}

@Composable
private fun MediaFileItem(
    index: Int,
    file: MediaFile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(text = file.name) },
        leadingContent = {
            Text(
                text = (index + 1).toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.width(24.dp)
            )
        },
        supportingContent = {
            Text(file.durationInMs?.asTimeText() ?: UndefinedTimeText)
        },
        modifier = modifier.clickable(
            onClick = onClick,
        )
    )
}

@Composable
private fun DeleteCollectionDialog(
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
        icon = {
            Icon(
                painter = painterResource(coreR.drawable.error_24dp_000000_fill0_wght400_grad0_opsz24),
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(R.string.home_media_collection_screen_delete_collection_title))
        },
        text = {
            Text(text = stringResource(R.string.home_media_collection_screen_delete_collection_desc))
        },
        modifier = modifier
    )
}