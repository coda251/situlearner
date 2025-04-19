package com.coda.situlearner.feature.word.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.data.mapper.asPlaylistItem
import com.coda.situlearner.core.testing.data.wordWithContextsListTestData
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.PlayOrPauseButton
import com.coda.situlearner.core.ui.widget.ProficiencyIconSet
import com.coda.situlearner.core.ui.widget.WordContextText
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun WordDetailScreen(
    onBack: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToWordEdit: (String) -> Unit,
    viewModel: WordDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()

    WordDetailScreen(
        uiState = uiState,
        playerState = playerState,
        onBack = {
            playerState.clear()
            onBack()
        },
        onViewWord = viewModel::setWordViewedDate,
        onNavigateToPlayer = onNavigateToPlayer,
        onNavigateToWordEdit = onNavigateToWordEdit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordDetailScreen(
    uiState: WordDetailUiState,
    playerState: PlayerState,
    onBack: () -> Unit,
    onViewWord: (Word) -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToWordEdit: (String) -> Unit,
) {
    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = { BackButton(onBack) },
                actions = {
                    when (uiState) {
                        is WordDetailUiState.Success -> {
                            IconButton(
                                onClick = {
                                    playerState.clear()
                                    onNavigateToWordEdit(uiState.wordWithContexts.word.id)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(coreR.drawable.edit_24dp_000000_fill0_wght400_grad0_opsz24),
                                    contentDescription = null
                                )
                            }
                        }

                        else -> {}
                    }
                },
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
                WordDetailUiState.Loading -> {}
                WordDetailUiState.Empty -> {}
                is WordDetailUiState.Success -> {
                    WordDetailContentBoard(
                        playerState = playerState,
                        wordWithContexts = uiState.wordWithContexts,
                        onNavigateToPlayer = onNavigateToPlayer,
                        onViewWord = onViewWord,
                    )
                }
            }
        }
    }
}

@Composable
private fun WordDetailContentBoard(
    playerState: PlayerState,
    wordWithContexts: WordWithContexts,
    onNavigateToPlayer: () -> Unit,
    onViewWord: (Word) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        playerState.clear()
        onViewWord(wordWithContexts.word)
    }

    var showBottomBar by remember { mutableStateOf(false) }

    var showPlayer by remember { mutableStateOf(false) }

    val isPlaying by playerState.isPlaying.collectAsStateWithLifecycle()

    Column(modifier = modifier) {

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            AnimatedVisibility(showPlayer) {
                Column {
                    PlayerViewCard(playerState)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            WordWithContextsCard(
                word = wordWithContexts.word,
                contexts = wordWithContexts.contexts,
                onClickWordContext = {
                    val mediaFile = it.mediaFile
                    val mediaCollection = it.mediaCollection
                    if (mediaCollection != null && mediaFile != null) {
                        val item = (mediaCollection to mediaFile).asPlaylistItem()
                        playerState.addItems(listOf(item), item)
                        playerState.play()
                        playerState.setPlaybackLoop(
                            start = it.wordContext.subtitleStartTimeInMs,
                            end = it.wordContext.subtitleEndTimeInMs
                        )
                        playerState.seekTo(it.wordContext.subtitleStartTimeInMs)
                        when (item.mediaType) {
                            MediaType.Video -> {
                                showPlayer = true
                                showBottomBar = true
                            }

                            MediaType.Audio -> {
                                showPlayer = false
                                showBottomBar = true
                            }
                        }
                    }
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
        ) {
            AnimatedVisibility(
                showBottomBar,
                enter = slideInVertically { it },
            ) {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.CenterVertically),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlayOrPauseButton(
                            isPlaying = isPlaying
                        ) {
                            if (it) playerState.play()
                            else playerState.pause()
                        }

                        IconButton(onNavigateToPlayer) {
                            Icon(
                                painter = painterResource(R.drawable.video_label_24dp_000000_fill0_wght400_grad0_opsz24),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerViewCard(
    playerState: PlayerState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        playerState.VideoOutput(modifier = Modifier)
    }
}

@Composable
private fun WordWithContextsCard(
    word: Word,
    contexts: List<WordContextView>,
    onClickWordContext: (WordContextView) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        ListItem(
            headlineContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ProficiencyIconSet(
                        modifier = Modifier.padding(vertical = 12.dp),
                        proficiency = word.proficiency,
                    )
                }
            },
            supportingContent = {
                Text(
                    text = word.pronunciation ?: "",
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        LazyColumn {
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.word_detail_screen_example_sentences),
                            modifier = Modifier.alpha(0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }

            items(
                items = contexts,
                key = { it.wordContext.id },
                contentType = { "examples" }
            ) {
                ListItem(
                    headlineContent = {
                        WordContextText(it.wordContext)
                    },
                    supportingContent = {
                        Text(text = it.mediaFile?.name ?: "")
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable { onClickWordContext(it) }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = coreR.string.core_ui_meanings),
                            modifier = Modifier.alpha(0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    trailingContent = {
                        Text(text = word.dictionaryName ?: "")
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }

            items(
                items = word.meanings,
                key = { it.partOfSpeechTag },
                contentType = { "meanings" }
            ) {
                ListItem(
                    headlineContent = {
                        Text(text = it.definition)
                    },
                    overlineContent = {
                        Text(text = it.partOfSpeechTag)
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

@Composable
@Preview
private fun WordDetailScreenPreview() {

    val uiState = WordDetailUiState.Success(
        wordWithContexts = wordWithContextsListTestData[0]
    )

    WordDetailScreen(
        playerState = PlayerStateProvider.EmptyPlayerState,
        uiState = uiState,
        onBack = {},
        onViewWord = {},
        onNavigateToPlayer = {},
        onNavigateToWordEdit = {}
    )
}