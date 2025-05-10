package com.coda.situlearner.feature.player.word

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.mapper.asWordInfo
import com.coda.situlearner.core.model.infra.RemoteWordInfoState
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.core.testing.data.wordContextsTestData
import com.coda.situlearner.core.testing.data.wordsTestData
import com.coda.situlearner.core.ui.widget.WordInfoDetailItem
import com.coda.situlearner.core.ui.widget.WordInfoEmptyItem
import com.coda.situlearner.core.ui.widget.WordTranslationBoard
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PlayerWordBottomSheet(
    onDismiss: () -> Unit,
    viewModel: PlayerWordViewModel = koinViewModel()
) {
    val route = viewModel.route
    val wordContextUiState by viewModel.wordContextUiState.collectAsStateWithLifecycle()
    val wordQueryUiState by viewModel.wordQueryUiState.collectAsStateWithLifecycle()
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()

    PlayerWordBottomSheet(
        word = route.word,
        mediaId = route.mediaId,
        playerState = playerState,
        wordContextUiState = wordContextUiState,
        wordQueryUiState = wordQueryUiState,
        onAddWordContext = viewModel::insertWordWithContext,
        onDeleteWordContext = viewModel::deleteWordContext,
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerWordBottomSheet(
    word: String,
    mediaId: String,
    playerState: PlayerState,
    wordContextUiState: WordContextUiState,
    wordQueryUiState: WordQueryUiState,
    onAddWordContext: (WordInfo?) -> Unit,
    onDeleteWordContext: (WordContext) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playlist by playerState.playlist.collectAsStateWithLifecycle()
    LaunchedEffect(playlist) {
        if (playlist.currentItem?.id != mediaId) onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
    ) {
        PlayerWordScreen(
            word = word,
            wordContextUiState = wordContextUiState,
            wordQueryUiState = wordQueryUiState,
            onAddWordContext = onAddWordContext,
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
private fun PlayerWordScreen(
    word: String,
    wordContextUiState: WordContextUiState,
    wordQueryUiState: WordQueryUiState,
    onAddWordContext: (WordInfo?) -> Unit,
    onDeleteWordContext: (WordContext) -> Unit,
    modifier: Modifier = Modifier
) {
    var transIndexToInfoIndex by remember(wordQueryUiState is WordQueryUiState.ResultWeb) {
        mutableStateOf<Pair<Int, Int?>>(0 to null)
    }

    val displayedWordInfo = when (wordQueryUiState) {
        is WordQueryUiState.ResultDb -> DisplayedWordInfoState.Result(wordQueryUiState.word.asWordInfo())
        is WordQueryUiState.ResultWeb -> {
            val translation = wordQueryUiState.translations[transIndexToInfoIndex.first]
            when (val infoState = translation.infoState) {
                is RemoteWordInfoState.Multiple -> {
                    val infoIndex = transIndexToInfoIndex.second
                    infoIndex?.let { infoState.infos.getOrNull(it) }?.let {
                        DisplayedWordInfoState.Result(it)
                    } ?: DisplayedWordInfoState.ShouldBeSpecified
                }

                is RemoteWordInfoState.Single -> DisplayedWordInfoState.Result(infoState.info)
                else -> DisplayedWordInfoState.Empty
            }
        }

        else -> DisplayedWordInfoState.Empty
    }

    val externalWord = when (wordContextUiState) {
        is WordContextUiState.Existed -> wordContextUiState.word.word.takeIf { it != word }
        is WordContextUiState.OnlyWord -> wordContextUiState.word.word.takeIf { it != word }
        else -> when (displayedWordInfo) {
            is DisplayedWordInfoState.Result -> displayedWordInfo.wordInfo.word.takeIf { it != word }
            else -> ""
        }
    }

    Column(modifier = modifier) {
        WordContextBoard(
            word = word,
            externalWord = externalWord,
            wordContextUiState = wordContextUiState,
            onAddWordContext = {
                when (displayedWordInfo) {
                    DisplayedWordInfoState.Empty -> onAddWordContext(null)
                    // if no valid infoIndex is provided, then do nothing
                    DisplayedWordInfoState.ShouldBeSpecified -> {}
                    is DisplayedWordInfoState.Result -> onAddWordContext(displayedWordInfo.wordInfo)
                }
            },
            onDeleteWordContext = onDeleteWordContext,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            when (wordQueryUiState) {
                WordQueryUiState.NoTranslatorError -> {}

                WordQueryUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                is WordQueryUiState.ResultDb -> wordQueryUiState.word.asWordInfo().let {
                    if (it.isNotEmpty()) {
                        WordInfoDetailItem(
                            wordInfo = it,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    } else {
                        WordInfoEmptyItem(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is WordQueryUiState.ResultWeb -> {
                    WordTranslationBoard(
                        translations = wordQueryUiState.translations,
                        onSelect = { transIndexToInfoIndex = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun WordContextBoard(
    word: String,
    externalWord: String?,
    wordContextUiState: WordContextUiState,
    onAddWordContext: () -> Unit,
    onDeleteWordContext: (WordContext) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = externalWord ?: "",
                    modifier = Modifier.animateContentSize(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        trailingContent = {
            when (wordContextUiState) {
                WordContextUiState.Loading -> {}
                is WordContextUiState.Existed -> IconButton(
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

                WordContextUiState.Empty, is WordContextUiState.OnlyWord -> IconButton(
                    onClick = onAddWordContext
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.bookmark_24dp_000000_fill0_wght400_grad0_opsz24
                        ),
                        contentDescription = null
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

private sealed interface DisplayedWordInfoState {
    data object Empty : DisplayedWordInfoState
    data object ShouldBeSpecified : DisplayedWordInfoState
    data class Result(val wordInfo: WordInfo) : DisplayedWordInfoState
}

@Composable
@Preview(showBackground = true)
private fun PlayerWordBottomSheetPreview() {

    val wordQueryUiState = WordQueryUiState.ResultDb(
        word = wordsTestData[0]
    )

    var wordContextUiState by remember {
        mutableStateOf<WordContextUiState>(
            WordContextUiState.Existed(
                word = wordsTestData[0],
                wordContext = wordContextsTestData[0]
            )
        )
    }

    PlayerWordBottomSheet(
        word = wordsTestData[0].word,
        mediaId = "",
        playerState = PlayerStateProvider.EmptyPlayerState,
        wordContextUiState = wordContextUiState,
        wordQueryUiState = wordQueryUiState,
        onDeleteWordContext = {
            wordContextUiState = WordContextUiState.Empty
        },
        onAddWordContext = {
            wordContextUiState = WordContextUiState.Existed(
                word = wordsTestData[0],
                wordContext = wordContextsTestData[0]
            )
        },
        onDismiss = {}
    )
}