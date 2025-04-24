package com.coda.situlearner.feature.player.word

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.mapper.asWordInfo
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.core.testing.data.wordContextsTestData
import com.coda.situlearner.core.testing.data.wordsTestData
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.WordItem
import com.coda.situlearner.feature.player.word.model.RemoteWordInfoState
import com.coda.situlearner.feature.player.word.model.Translation
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import com.coda.situlearner.infra.subkit.translator.Translator
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

    Column(modifier = modifier) {
        WordContextBoard(
            word = word,
            wordContextUiState = wordContextUiState,
            onAddWordContext = {
                when (wordQueryUiState) {
                    is WordQueryUiState.ResultDb -> wordQueryUiState.word.asWordInfo().let {
                        onAddWordContext(it)
                    }

                    is WordQueryUiState.ResultWeb -> {
                        val translation = wordQueryUiState.translations[transIndexToInfoIndex.first]
                        when (val infoState = translation.infoState) {
                            is RemoteWordInfoState.Multiple -> {
                                val infoIndex = transIndexToInfoIndex.second
                                // if no valid infoIndex is provided, do not add wordContext
                                infoIndex?.let { infoState.infos.getOrNull(it) }
                                    ?.let(onAddWordContext)
                            }

                            is RemoteWordInfoState.Single -> onAddWordContext(infoState.info)
                            else -> onAddWordContext(null)
                        }

                    }

                    else -> {
                        onAddWordContext(null)
                    }
                }
            },
            onDeleteWordContext = onDeleteWordContext
        )

        Box(modifier = Modifier.weight(1f)) {
            when (wordQueryUiState) {
                WordQueryUiState.NoTranslatorError -> {}

                WordQueryUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                is WordQueryUiState.ResultDb -> wordQueryUiState.word.asWordInfo().let {
                    WordInfoDetailItem(
                        wordInfo = it,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
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
private fun WordTranslationBoard(
    translations: List<Translation>,
    onSelect: (Pair<Int, Int?>) -> Unit
) {
    var translationIndex by remember {
        mutableIntStateOf(0)
    }

    var infoIndexes by remember {
        mutableStateOf<List<Int?>>(
            translations.map { null }.toList()
        )
    }

    Column {
        TranslatorChipsPanel(
            translators = translations.map { it.translator },
            selectedTranslatorIndex = translationIndex,
            onSelect = {
                translationIndex = it
                onSelect(translationIndex to infoIndexes[translationIndex])
            }
        )

        TranslationResultPanel(
            translation = translations[translationIndex],
            selectedInfoIndex = infoIndexes[translationIndex],
            onSelectInfoIndex = {
                infoIndexes = infoIndexes.toMutableList().apply {
                    this[translationIndex] = it
                }
                onSelect(translationIndex to it)
            }
        )
    }
}

@Composable
private fun WordContextBoard(
    word: String,
    wordContextUiState: WordContextUiState,
    onAddWordContext: () -> Unit,
    onDeleteWordContext: (WordContext) -> Unit,
) {
    ListItem(
        modifier = Modifier.padding(horizontal = 12.dp),
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(8.dp))

                AnimatedContent(targetState = wordContextUiState) { targetState ->
                    when (targetState) {
                        is WordContextUiState.Existed -> {
                            val wordInDb = targetState.word.word
                            Text(
                                text = if (wordInDb != word) wordInDb else "",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        else -> {
                        }
                    }
                }
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

@Composable
private fun TranslatorChipsPanel(
    translators: List<Translator>,
    selectedTranslatorIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 28.dp)
    ) {
        itemsIndexed(
            items = translators,
            key = { _, item -> item.name }
        ) { index, it ->
            FilterChip(
                onClick = { onSelect(index) },
                label = { Text(it.name) },
                selected = index == selectedTranslatorIndex,
            )
        }
    }
}

@Composable
private fun TranslationResultPanel(
    translation: Translation,
    selectedInfoIndex: Int?,
    onSelectInfoIndex: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (val wordInfoState = translation.infoState) {
            RemoteWordInfoState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            RemoteWordInfoState.Empty -> {
                WordInfoEmpty(modifier = Modifier.align(Alignment.Center))
            }

            RemoteWordInfoState.Error -> {}
            is RemoteWordInfoState.Single -> {
                WordInfoDetailItem(
                    wordInfo = wordInfoState.info,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            is RemoteWordInfoState.Multiple -> {
                WordInfosPanel(
                    wordInfos = wordInfoState.infos,
                    selectedInfoIndex = selectedInfoIndex,
                    onSelect = onSelectInfoIndex,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun WordInfoEmpty(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.player_entry_screen_no_web_meanings)
    )
}

@Composable
private fun WordInfosPanel(
    wordInfos: List<WordInfo>,
    selectedInfoIndex: Int?,
    onSelect: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentSelectedInfo = selectedInfoIndex?.let { wordInfos.getOrNull(it) }

    if (currentSelectedInfo != null) {
        WordInfoDetailItem(
            wordInfo = currentSelectedInfo,
            modifier = modifier,
            onBack = {
                onSelect(null)
            }
        )
    } else {
        LazyColumn(modifier = modifier) {
            itemsIndexed(
                items = wordInfos,
            ) { index, it ->
                WordItem(
                    word = it.word,
                    modifier = Modifier.clickable { onSelect(index) },
                    pronunciation = it.pronunciation,
                    definition = it.meanings.firstOrNull()?.definition,
                )
            }
        }
    }
}

@Composable
private fun WordInfoDetailItem(
    wordInfo: WordInfo,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    if (wordInfo.pronunciation == null && wordInfo.meanings.isEmpty()) {
        Box(modifier = modifier.fillMaxSize()) {
            WordInfoEmpty(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Column(modifier = modifier) {
            if (onBack != null) {
                ListItem(
                    headlineContent = { Text(text = wordInfo.pronunciation ?: "") },
                    trailingContent = {
                        BackButton(onBack = onBack, rotated = true)
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            } else {
                wordInfo.pronunciation?.let {
                    ListItem(
                        headlineContent = { Text(text = it) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }

            wordInfo.meanings.let {
                LazyColumn {
                    items(
                        items = it,
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