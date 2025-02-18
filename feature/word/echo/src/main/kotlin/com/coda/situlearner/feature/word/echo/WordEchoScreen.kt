package com.coda.situlearner.feature.word.echo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.testing.data.wordContextsTestData
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.WordContextText
import com.coda.situlearner.infra.player.PlayerState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WordEchoScreen(
    onBack: () -> Unit,
    viewModel: WordEchoViewModel = koinViewModel()
) {
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WordEchoScreen(
        playerState = playerState,
        uiState = uiState,
        onBack = onBack
    )
}

@Composable
private fun WordEchoScreen(
    playerState: PlayerState,
    uiState: WordEchoUiState,
    onBack: () -> Unit
) {
    val isPlaying by playerState.isPlaying.collectAsStateWithLifecycle()
    val playlist by playerState.playlist.collectAsStateWithLifecycle()

    val currentId by remember { derivedStateOf { playlist.currentItem?.id } }

    WordEchoScreen(
        isPlaying = isPlaying,
        currentId = currentId,
        uiState = uiState,
        onBack = {
            playerState.clear()
            playerState.setRepeatNumber(1)
            onBack()
        },
        onToggleShouldBePlaying = {
            if (it) playerState.play()
            else playerState.pause()
        },
        onSeekTo = playerState::seekToItem
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordEchoScreen(
    isPlaying: Boolean,
    currentId: String?,
    uiState: WordEchoUiState,
    onBack: () -> Unit,
    onToggleShouldBePlaying: (Boolean) -> Unit,
    onSeekTo: (Int) -> Unit
) {
    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton { onBack() }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            when (uiState) {
                WordEchoUiState.Empty, WordEchoUiState.Loading -> {}
                is WordEchoUiState.Success -> {
                    WordEchoContentBoard(
                        isPlaying = isPlaying,
                        currentId = currentId,
                        wordContexts = uiState.wordContexts,
                        onToggleShouldBePlaying = onToggleShouldBePlaying,
                        onSeekTo = onSeekTo,
                    )
                }
            }
        }
    }
}

@Composable
private fun WordEchoContentBoard(
    isPlaying: Boolean,
    currentId: String?,
    wordContexts: List<WordContext>,
    onToggleShouldBePlaying: (Boolean) -> Unit,
    onSeekTo: (Int) -> Unit,
) {
    LazyColumn {
        itemsIndexed(
            items = wordContexts,
            key = { _, it -> it.id },
        ) { index, it ->
            val isCurrent = currentId == it.id

            ListItem(
                headlineContent = {
                    WordContextText(it)
                },
                modifier = Modifier.clickable {
                    if (isCurrent) {
                        onToggleShouldBePlaying(!isPlaying)
                    } else {
                        onToggleShouldBePlaying(true)
                        onSeekTo(index)
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = if (isCurrent) MaterialTheme.colorScheme.surfaceVariant
                    else Color.Transparent
                )
            )
        }
    }
}

@Preview
@Composable
private fun WordEchoScreenPreview() {

    var isPlaying by remember {
        mutableStateOf(false)
    }

    var currentId by remember {
        mutableStateOf("0")
    }

    val uiState = WordEchoUiState.Success(wordContexts = wordContextsTestData)

    WordEchoScreen(
        isPlaying = true,
        currentId = currentId,
        uiState = uiState,
        onBack = {},
        onToggleShouldBePlaying = {
            isPlaying = it
        },
        onSeekTo = {
            currentId = uiState.wordContexts[it].id
        }
    )
}