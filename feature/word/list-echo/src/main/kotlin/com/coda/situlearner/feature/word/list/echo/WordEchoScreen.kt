package com.coda.situlearner.feature.word.list.echo

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.testing.data.wordContextsTestData
import com.coda.situlearner.core.testing.data.wordWithContextsListTestData
import com.coda.situlearner.core.ui.R
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.WordContextText
import com.coda.situlearner.core.ui.widget.WordItem
import com.coda.situlearner.infra.player.PlayerState
import kotlinx.coroutines.launch
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
    val playWhenReady by playerState.playWhenReady.collectAsStateWithLifecycle()
    val playlist by playerState.playlist.collectAsStateWithLifecycle()

    WordEchoScreen(
        playWhenReady = playWhenReady,
        currentIndex = playlist.currentIndex,
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
    playWhenReady: Boolean,
    currentIndex: Int,
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
                    BackButton(onBack)
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
                        playWhenReady = playWhenReady,
                        currentIndex = currentIndex,
                        wordContexts = uiState.words,
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
    playWhenReady: Boolean,
    currentIndex: Int,
    wordContexts: List<Pair<Word, WordContext>>,
    onToggleShouldBePlaying: (Boolean) -> Unit,
    onSeekTo: (Int) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp), // leave space for floating button
            state = lazyListState
        ) {
            itemsIndexed(
                items = wordContexts,
                key = { _, it -> it.second.id },
            ) { index, it ->
                val isCurrent = currentIndex == index

                WordItem(
                    word = it.first,
                    showProficiency = false,
                    modifier = Modifier
                        .clickable {
                            if (isCurrent) {
                                onToggleShouldBePlaying(!playWhenReady)
                            } else {
                                onToggleShouldBePlaying(true)
                                onSeekTo(index)
                            }
                        }
                        .background(
                            if (isCurrent) MaterialTheme.colorScheme.secondaryContainer
                            else Color.Transparent
                        )
                )
            }
        }

        wordContexts.getOrNull(currentIndex)?.let { pair ->
            WordEcho(
                wordContext = pair.second,
                playWhenReady = playWhenReady,
                onClick = {
                    if (it) onToggleShouldBePlaying(true)
                    else {
                        scope.launch {
                            lazyListState.scrollToItem(currentIndex)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun WordEcho(
    wordContext: WordContext,
    playWhenReady: Boolean,
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { onClick(!playWhenReady) },
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            AnimatedContent(
                targetState = playWhenReady,
                contentAlignment = Alignment.BottomEnd,
                transitionSpec = {
                    if (targetState) {
                        ContentTransform(
                            targetContentEnter = expandHorizontally(expandFrom = Alignment.End) + expandVertically(
                                expandFrom = Alignment.Bottom
                            ),
                            initialContentExit = fadeOut()
                        )
                    } else {
                        ContentTransform(
                            targetContentEnter = fadeIn(),
                            initialContentExit = shrinkHorizontally(shrinkTowards = Alignment.End) + shrinkVertically(
                                shrinkTowards = Alignment.Bottom
                            )
                        )
                    }
                }
            ) {
                if (it) {
                    ListItem(
                        headlineContent = { WordContextText(wordContext) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.animateContentSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.play_arrow_24dp_000000_fill1_wght400_grad0_opsz24),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun WordEchoScreenPreview() {
    var playWhenReady by remember {
        mutableStateOf(false)
    }

    var currentIndex by remember {
        mutableIntStateOf(0)
    }

    val uiState = WordEchoUiState.Success(words = wordWithContextsListTestData.mapNotNull {
        it.contexts.randomOrNull()?.let { context -> it.word to context.wordContext }
    })

    WordEchoScreen(
        playWhenReady = playWhenReady,
        currentIndex = currentIndex,
        uiState = uiState,
        onBack = {},
        onToggleShouldBePlaying = {
            playWhenReady = it
        },
        onSeekTo = {
            currentIndex = it
        }
    )
}

@Preview
@Composable
private fun FloatingWordEchoPreview() {
    var playWhenReady by remember {
        mutableStateOf(true)
    }

    val wordContextIndex by remember {
        mutableIntStateOf(1)
    }

    WordEcho(
        wordContext = wordContextsTestData[wordContextIndex],
        playWhenReady = playWhenReady,
        onClick = {
            // wordContextIndex = 1 - wordContextIndex
            playWhenReady = it
        },
        modifier = Modifier.fillMaxSize()
    )
}