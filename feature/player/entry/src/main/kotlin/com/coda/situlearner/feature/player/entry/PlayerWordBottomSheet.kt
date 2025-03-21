package com.coda.situlearner.feature.player.entry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.PartOfSpeech
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.testing.data.wordContextsTestData
import com.coda.situlearner.core.testing.data.wordsTestData
import org.koin.compose.getKoin

@Composable
internal fun PlayerWordBottomSheet(
    route: PlayerWordBottomSheetRoute,
    onDismiss: () -> Unit
) {
    // TODO: wait for navGraphBuilder.bottomSheet, see https://issuetracker.google.com/issues/376169507
    // we need the lifecycle of this viewModel binds with the composable (not the screen),
    // if the composable dismisses, the viewModel should be destroyed. However, if screen
    // rotates, the viewModel will be recreated. So this is a workaround currently
    val koin = getKoin()
    val viewModel = remember { PlayerWordViewModel(route, koin.get()) }

    val wordContextUiState by viewModel.wordContextUiState.collectAsStateWithLifecycle()
    val wordInfoUiState by viewModel.wordInfoUiState.collectAsStateWithLifecycle()

    PlayerWordBottomSheet(
        word = route.word,
        wordContextUiState = wordContextUiState,
        wordInfoUiState = wordInfoUiState,
        onAddWordContext = viewModel::insertWordWithContext,
        onDeleteWordContext = viewModel::deleteWordContext,
        onChangePOSOfWordContext = viewModel::updateWordContextPOS,
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerWordBottomSheet(
    word: String,
    wordContextUiState: WordContextUiState,
    wordInfoUiState: WordInfoUiState,
    onAddWordContext: (WordInfoUiState, PartOfSpeech) -> Unit,
    onChangePOSOfWordContext: (WordContext, PartOfSpeech) -> Unit,
    onDeleteWordContext: (WordContext) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
    ) {
        WordContextBoard(
            word = word,
            wordContextUiState = wordContextUiState,
            wordInfoUiState = wordInfoUiState,
            onSelectPOS = {
                when (wordContextUiState) {
                    is WordContextUiState.Success -> onChangePOSOfWordContext(
                        wordContextUiState.wordContext,
                        it
                    )

                    is WordContextUiState.Empty -> onAddWordContext(wordInfoUiState, it)
                    else -> {}
                }
            },
            onDeleteWordContext = onDeleteWordContext,
            modifier = modifier
                .height(350.dp)
                .padding(top = 12.dp, bottom = 28.dp)
                .nestedScroll(object : NestedScrollConnection {
                    override fun onPostScroll(
                        consumed: Offset, available: Offset, source: NestedScrollSource
                    ): Offset = available
                })
        )
    }
}

@Composable
private fun WordContextBoard(
    word: String,
    wordContextUiState: WordContextUiState,
    wordInfoUiState: WordInfoUiState,
    onSelectPOS: (PartOfSpeech) -> Unit,
    onDeleteWordContext: (WordContext) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPOSChips by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(wordContextUiState) {
        showPOSChips = when (wordContextUiState) {
            WordContextUiState.Loading, WordContextUiState.Empty -> {
                false
            }

            is WordContextUiState.Success -> {
                true
            }
        }
    }

    Column(modifier = modifier) {
        ListItem(
            modifier = Modifier.padding(horizontal = 12.dp),
            headlineContent = {
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            trailingContent = {
                when (wordContextUiState) {
                    WordContextUiState.Loading -> {}
                    WordContextUiState.Empty -> IconButton(
                        onClick = { showPOSChips = true }
                    ) {
                        Icon(
                            painter = painterResource(
                                R.drawable.bookmark_24dp_000000_fill0_wght400_grad0_opsz24
                            ),
                            contentDescription = null
                        )
                    }

                    is WordContextUiState.Success -> IconButton(
                        onClick = {
                            onDeleteWordContext(wordContextUiState.wordContext)
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                R.drawable.bookmark_24dp_000000_fill1_wght400_grad0_opsz24
                            ),
                            contentDescription = null
                        )
                    }
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        AnimatedVisibility(visible = showPOSChips) {
            WordPOSChips(
                selectedPOS = when (wordContextUiState) {
                    is WordContextUiState.Success -> wordContextUiState.wordContext.partOfSpeech
                    else -> null
                },
                onSelectPOS = onSelectPOS,
            )
        }

        WordInfoBoard(
            wordInfoUiState = wordInfoUiState,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}

@Composable
private fun WordInfoBoard(
    wordInfoUiState: WordInfoUiState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (wordInfoUiState) {
            WordInfoUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            WordInfoUiState.Error -> {}
            is WordInfoUiState.Empty -> {}
            is WordInfoUiState.Success -> {
                Column {
                    wordInfoUiState.pronunciation?.let {
                        ListItem(
                            headlineContent = { Text(text = it) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }

                    LazyColumn {
                        items(
                            items = wordInfoUiState.meanings,
                            key = { it.partOfSpeechTag }
                        ) {
                            ListItem(
                                headlineContent = { Text(text = it.definition) },
                                overlineContent = { Text(text = it.partOfSpeechTag) },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WordPOSChips(
    selectedPOS: PartOfSpeech?,
    onSelectPOS: (PartOfSpeech) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 28.dp)
    ) {
        items(
            items = PartOfSpeech.entries.sortedBy { it.level },
            key = { it.name }
        ) {
            FilterChip(
                onClick = { onSelectPOS(it) },
                label = { Text(it.asText()) },
                selected = it == selectedPOS,
            )
        }
    }
}

@Composable
private fun PartOfSpeech.asText(): String = when (this) {
    PartOfSpeech.Unknown -> stringResource(R.string.player_entry_screen_part_of_speech_unknown)
    PartOfSpeech.Noun -> stringResource(R.string.player_entry_screen_part_of_speech_noun)
    PartOfSpeech.Verb -> stringResource(R.string.player_entry_screen_part_of_speech_verb)
    PartOfSpeech.Adjective -> stringResource(R.string.player_entry_screen_part_of_speech_adjective)
    PartOfSpeech.Adverb -> stringResource(R.string.player_entry_screen_part_of_speech_adverb)
}

@Composable
@Preview(showBackground = true)
private fun PlayerWordBottomSheetPreview() {

    val wordInfoUiState = WordInfoUiState.Success(
        dictionaryName = wordsTestData[0].dictionaryName!!,
        pronunciation = wordsTestData[0].pronunciation!!,
        meanings = wordsTestData[0].meanings!!
    )

    var wordContextUiState by remember {
        mutableStateOf<WordContextUiState>(
            WordContextUiState.Success(
                wordContext = wordContextsTestData[0]
            )
        )
    }

    PlayerWordBottomSheet(
        word = wordsTestData[0].word,
        wordContextUiState = wordContextUiState,
        wordInfoUiState = wordInfoUiState,
        onDeleteWordContext = {
            wordContextUiState = WordContextUiState.Empty
        },
        onChangePOSOfWordContext = { _, pos ->
            wordContextUiState = WordContextUiState.Success(
                wordContext = wordContextsTestData[0].copy(partOfSpeech = pos)
            )
        },
        onAddWordContext = { _, pos ->
            wordContextUiState = WordContextUiState.Success(
                wordContext = wordContextsTestData[0].copy(partOfSpeech = pos)
            )
        },
        onDismiss = {}
    )
}