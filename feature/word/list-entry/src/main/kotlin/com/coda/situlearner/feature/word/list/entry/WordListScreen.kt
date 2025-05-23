package com.coda.situlearner.feature.word.list.entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.RepeatMode
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.core.testing.data.wordWithContextsListTestData
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.WordItem
import com.coda.situlearner.feature.word.list.entry.model.WordSortBy
import com.coda.situlearner.feature.word.list.entry.model.toPlaylistItems
import com.coda.situlearner.feature.word.list.entry.navigation.WordListEntryRoute
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun WordListScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToWordEcho: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WordListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()

    WordListScreen(
        uiState = uiState,
        route = viewModel.route,
        playerState = playerState,
        onBack = onBack,
        onClickWord = { onNavigateToWordDetail(it.id) },
        onDeleteWord = viewModel::deleteWord,
        onRepeatWordContexts = { onNavigateToWordEcho() },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordListScreen(
    uiState: WordListUiState,
    route: WordListEntryRoute,
    playerState: PlayerState,
    onBack: () -> Unit,
    onClickWord: (Word) -> Unit,
    onDeleteWord: (Word) -> Unit,
    onRepeatWordContexts: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOptionBottomSheet by remember { mutableStateOf(false) }

    var currentWord by remember { mutableStateOf<Word?>(null) }
    var showWordBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onBack) },
                actions = {
                    when (uiState) {
                        WordListUiState.Empty -> {}
                        WordListUiState.Loading -> {}
                        is WordListUiState.Success -> {
                            IconButton(
                                onClick = {
                                    showOptionBottomSheet = true
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.filter_list_24dp_000000_fill0_wght400_grad0_opsz24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            if (uiState is WordListUiState.Success) {
                FloatingActionButton(
                    onClick = {
                        val items = uiState.data.toPlaylistItems(
                            wordListType = route.wordListType,
                            id = route.id
                        )
                        if (items.isNotEmpty()) {
                            playerState.setRepeatMode(RepeatMode.All)
                            playerState.setRepeatNumber(3)
                            playerState.setItems(items)
                            playerState.play()
                            onRepeatWordContexts()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(coreR.drawable.playlist_play_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = null
                    )
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            when (uiState) {
                WordListUiState.Empty -> {}
                WordListUiState.Loading -> {}
                is WordListUiState.Success -> {
                    ContentBoard(
                        words = uiState.data,
                        showProficiency = uiState.wordSortBy == WordSortBy.Proficiency,
                        onClickWord = onClickWord,
                        onLongClickWord = {
                            currentWord = it
                            showWordBottomSheet = true
                        }
                    )
                }
            }
        }
    }

    if (showOptionBottomSheet) {
        WordOptionBottomSheet(onDismiss = {
            showOptionBottomSheet = false
        })
    }

    if (showWordBottomSheet) {
        currentWord?.let { word ->
            WordBottomSheet(word = word, onDismiss = {
                showWordBottomSheet = false
            }) {
                showWordBottomSheet = false
                onDeleteWord(it)
            }
        }
    }
}

@Composable
private fun ContentBoard(
    words: List<WordWithContexts>,
    showProficiency: Boolean,
    onClickWord: (Word) -> Unit,
    onLongClickWord: (Word) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        items(
            items = words.map { it.word },
            key = { it.id }
        ) {
            WordItem(
                word = it,
                showProficiency = showProficiency,
                modifier = Modifier.combinedClickable(
                    onLongClick = { onLongClickWord(it) },
                    onClick = { onClickWord(it) }
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordBottomSheet(
    word: Word,
    onDismiss: () -> Unit,
    onDelete: (Word) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = {}
    ) {
        ListItem(
            headlineContent = {
                Text(word.word)
            }
        )
        ListItem(
            leadingContent = {
                Icon(
                    painter = painterResource(coreR.drawable.delete_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            },
            headlineContent = {
                Text(text = stringResource(coreR.string.core_ui_delete))
            },
            modifier = Modifier.clickable {
                onDelete(word)
            }
        )
    }
}

@Preview
@Composable
private fun WordListScreenPreview() {
    val uiState = WordListUiState.Success(
        wordSortBy = WordSortBy.Proficiency,
        data = wordWithContextsListTestData.sortedBy { it.word.meaningProficiency }
    )

    val route = WordListEntryRoute(
        wordListType = WordListType.MediaCollection,
        id = "0"
    )

    WordListScreen(
        uiState = uiState,
        route = route,
        playerState = PlayerStateProvider.EmptyPlayerState,
        onBack = {},
        onClickWord = {},
        onDeleteWord = {},
        onRepeatWordContexts = {}
    )
}