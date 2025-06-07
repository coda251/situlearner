package com.coda.situlearner.feature.home.media.collection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun MediaCollectionScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MediaCollectionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()

    MediaCollectionScreen(
        uiState = uiState,
        playerState = playerState,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaCollectionScreen(
    uiState: MediaCollectionUiState,
    playerState: PlayerState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (uiState) {
                            MediaCollectionUiState.Error -> ""
                            MediaCollectionUiState.Loading -> ""
                            is MediaCollectionUiState.Success -> uiState.collectionWithFiles.collection.name
                            is MediaCollectionUiState.Empty -> uiState.collection.name
                        }
                    )
                },
                navigationIcon = {
                    BackButton(onBack = onBack)
                },
                actions = {
                    when (uiState) {
                        is MediaCollectionUiState.Success -> {
                            MoreActionMenu {
                                playerState.setItems(uiState.collectionWithFiles.asPlaylist())
                                playerState.play()
                            }
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
                is MediaCollectionUiState.Empty -> {}
                is MediaCollectionUiState.Success -> {
                    MediaCollectionContentBoard(
                        files = uiState.collectionWithFiles.files,
                        onClickMediaFile = {
                            (uiState.collectionWithFiles.collection to it).asPlaylistItem().apply {
                                playerState.addItems(listOf(this), this)
                                playerState.play()
                            }
                        }
                    )
                }
            }
        }
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
private fun MoreActionMenu(onSetPlaylist: () -> Unit) {
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
        onDismissRequest = { showFileListMenu = false }) {
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