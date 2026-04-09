package com.coda.situlearner.feature.word.detail.entry

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.data.mapper.asPlaylistItem
import com.coda.situlearner.core.model.data.mapper.proficiencyType
import com.coda.situlearner.core.testing.data.wordWithContextsListTestData
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.PlayOrPauseButton
import com.coda.situlearner.core.ui.widget.ProficiencyIconSet
import com.coda.situlearner.core.ui.widget.WordContextText
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun WordDetailScreen(
    onBack: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToWordEdit: (String) -> Unit,
    onNavigateToWordRelation: (String) -> Unit,
    viewModel: WordDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    WordDetailScreen(
        uiState = uiState,
        playerState = playerState,
        actionState = actionState,
        onBack = {
            playerState.clear()
            onBack()
        },
        onViewWord = viewModel::setWordViewedDate,
        onDeleteWord = viewModel::deleteWord,
        onNavigateToPlayer = onNavigateToPlayer,
        onNavigateToWordEdit = onNavigateToWordEdit,
        onNavigateToWordRelation = onNavigateToWordRelation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordDetailScreen(
    uiState: WordDetailUiState,
    playerState: PlayerState,
    actionState: ActionState,
    onBack: () -> Unit,
    onViewWord: (Word) -> Unit,
    onDeleteWord: (Word) -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToWordEdit: (String) -> Unit,
    onNavigateToWordRelation: (String) -> Unit,
) {
    BackHandler {
        onBack()
    }

    LaunchedEffect(actionState) {
        // it's quick for deletion so we don't add block ui to avoid flash
        if (actionState is ActionState.Deleted) {
            onBack()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

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
                                    onNavigateToWordRelation(uiState.wordWithContexts.word.id)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.linked_services_24dp_000000_fill0_wght400_grad0_opsz24),
                                    contentDescription = null
                                )
                            }

                            MoreActionMenu(
                                onEditWord = {
                                    playerState.clear()
                                    onNavigateToWordEdit(uiState.wordWithContexts.word.id)
                                },
                                onDeleteWord = {
                                    showDeleteDialog = true
                                }
                            )
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
                        wordProficiencyType = uiState.wordProficiencyType,
                        onNavigateToPlayer = onNavigateToPlayer,
                        onViewWord = onViewWord,
                    )
                }
            }
        }
    }

    if (showDeleteDialog && uiState is WordDetailUiState.Success) {
        DeleteWordDialog(
            onConfirm = {
                onDeleteWord(uiState.wordWithContexts.word)
                showDeleteDialog = false
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}

@Composable
private fun WordDetailContentBoard(
    playerState: PlayerState,
    wordWithContexts: WordWithContexts,
    wordProficiencyType: WordProficiencyType,
    onNavigateToPlayer: () -> Unit,
    onViewWord: (Word) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        playerState.clear()
        // two concerns:
        // 1. immediately update word property will cause ui lag during transition
        // 2. quickly leaving the screen after entering should not be regarded as an effective view
        delay(2000L)
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
                wordProficiencyType = wordProficiencyType,
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
                BottomAppBar(modifier = Modifier.clickable { onNavigateToPlayer() }) {
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

                        Spacer(modifier = Modifier.size(48.dp))
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
    wordProficiencyType: WordProficiencyType,
    contexts: List<WordContextView>,
    onClickWordContext: (WordContextView) -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuizStats by remember { mutableStateOf(false) }

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
                        modifier = Modifier
                            .clickable { showQuizStats = true }
                            .padding(vertical = 12.dp),
                        proficiency = word.proficiency(wordProficiencyType),
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

    if (showQuizStats) {
        QuizStatsBottomSheet(onDismiss = { showQuizStats = false })
    }
}

@Composable
private fun MoreActionMenu(
    onEditWord: () -> Unit,
    onDeleteWord: () -> Unit,
) {
    var showMenu by remember {
        mutableStateOf(false)
    }

    IconButton(onClick = { showMenu = true }) {
        Icon(
            painter = painterResource(coreR.drawable.more_vert_24dp_000000_fill0_wght400_grad0_opsz24),
            contentDescription = null
        )
    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.word_detail_screen_edit_word)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(coreR.drawable.edit_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            },
            onClick = {
                onEditWord()
                showMenu = false
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
                onDeleteWord()
                showMenu = false
            }
        )
    }
}

@Composable
private fun DeleteWordDialog(
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
            Text(text = stringResource(R.string.word_detail_screen_delete_word_title))
        },
        text = {
            Text(text = stringResource(R.string.word_detail_screen_delete_word_desc))
        },
        modifier = modifier
    )
}

@Composable
@Preview
private fun WordDetailScreenPreview() {

    val uiState = WordDetailUiState.Success(
        wordWithContexts = wordWithContextsListTestData[0],
        wordProficiencyType = wordWithContextsListTestData[0].word.proficiencyType
    )

    WordDetailScreen(
        playerState = PlayerStateProvider.EmptyPlayerState,
        uiState = uiState,
        actionState = ActionState.Idle,
        onBack = {},
        onViewWord = {},
        onDeleteWord = {},
        onNavigateToPlayer = {},
        onNavigateToWordEdit = {},
        onNavigateToWordRelation = {}
    )
}