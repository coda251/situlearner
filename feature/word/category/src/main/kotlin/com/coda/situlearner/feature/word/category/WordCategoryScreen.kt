package com.coda.situlearner.feature.word.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.core.model.data.RepeatMode
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.testing.data.wordWithContextsListTestData
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.ProficiencyIconSet
import com.coda.situlearner.feature.word.category.model.CategoryViewType
import com.coda.situlearner.feature.word.category.model.MediaFileWithWords
import com.coda.situlearner.feature.word.category.model.WordSortBy
import com.coda.situlearner.feature.word.category.model.toMediaFileWithWords
import com.coda.situlearner.feature.word.category.model.toPlaylistItems
import com.coda.situlearner.feature.word.category.navigation.WordCategoryRoute
import com.coda.situlearner.feature.word.category.util.formatInstant
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WordCategoryScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToWordEcho: () -> Unit,
    onNavigateToWordCategory: (WordCategoryType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WordCategoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()

    WordCategoryScreen(
        uiState = uiState,
        route = viewModel.route,
        playerState = playerState,
        onBack = onBack,
        onClickWord = { onNavigateToWordDetail(it.id) },
        onClickFile = { onNavigateToWordCategory(WordCategoryType.MediaFile, it.id) },
        onRepeatWordContexts = { onNavigateToWordEcho() },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordCategoryScreen(
    uiState: WordCategoryUiState,
    route: WordCategoryRoute,
    playerState: PlayerState,
    onBack: () -> Unit,
    onClickWord: (Word) -> Unit,
    onClickFile: (MediaFile) -> Unit,
    onRepeatWordContexts: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOptionBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onBack) },
                actions = {
                    when (uiState) {
                        WordCategoryUiState.Empty -> {}
                        WordCategoryUiState.Loading -> {}
                        is WordCategoryUiState.Success -> {
                            IconButton(
                                onClick = {
                                    playerState.setRepeatMode(RepeatMode.All)
                                    playerState.setRepeatNumber(3)
                                    playerState.setItems(
                                        uiState.data.toPlaylistItems(
                                            categoryType = route.categoryType,
                                            categoryId = route.categoryId
                                        )
                                    )
                                    playerState.play()
                                    onRepeatWordContexts()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.event_repeat_24dp_000000_fill0_wght400_grad0_opsz24),
                                    contentDescription = null
                                )
                            }
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
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            when (uiState) {
                WordCategoryUiState.Empty -> {}
                WordCategoryUiState.Loading -> {}
                is WordCategoryUiState.Success -> {
                    when (uiState.viewType) {
                        CategoryViewType.NoGroup -> CategoryNoGroup(
                            words = uiState.data.filterIsInstance<WordWithContexts>(),
                            showProficiency = uiState.wordSortBy == WordSortBy.Proficiency,
                            onClickWord = onClickWord
                        )

                        CategoryViewType.GroupByMediaFile -> CategoryGroupWithMedia(
                            fileWithWords = uiState.data.filterIsInstance<MediaFileWithWords>(),
                            showProficiency = uiState.wordSortBy == WordSortBy.Proficiency,
                            onClickWord = onClickWord,
                            onClickFile = onClickFile,
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
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryGroupWithMedia(
    fileWithWords: List<MediaFileWithWords>,
    showProficiency: Boolean,
    onClickWord: (Word) -> Unit,
    onClickFile: (MediaFile) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        fileWithWords.onEach { mediaFileWithWords ->
            stickyHeader {
                ListItem(
                    headlineContent = {
                        Text(
                            text = mediaFileWithWords.file.name,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.clickable {
                        onClickFile(mediaFileWithWords.file)
                    }
                )
            }

            items(
                items = mediaFileWithWords.wordWithContextsList.map { it.word },
                key = { it.id + mediaFileWithWords.file.id }
            ) {
                WordItem(
                    word = it,
                    showProficiency = showProficiency,
                    modifier = Modifier
                        .clickable { onClickWord(it) }
                        // as the vertical padding for two line list item
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryNoGroup(
    words: List<WordWithContexts>,
    showProficiency: Boolean,
    onClickWord: (Word) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(
            items = words.map { it.word },
            key = { it.id }
        ) {
            WordItem(
                word = it,
                showProficiency = showProficiency,
                modifier = Modifier
                    .clickable { onClickWord(it) }
                    // as the vertical padding for two line list item
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun WordItem(
    word: Word,
    showProficiency: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = word.word,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = word.meanings?.firstOrNull()?.definition ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showProficiency) {
                Spacer(modifier = Modifier.width(8.dp))
                ProficiencyIconSet(
                    proficiency = word.proficiency,
                    onlyShowStarred = true,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.75f)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = word.pronunciation ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = word.lastViewedDate?.let {
                    formatInstant(it)
                } ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview
@Composable
private fun WordCategoryScreenPreview() {
    val uiState = WordCategoryUiState.Success(
        viewType = CategoryViewType.GroupByMediaFile,
        wordSortBy = WordSortBy.Proficiency,
        data = wordWithContextsListTestData.toMediaFileWithWords("0") { it.word.lastViewedDate }
    )

    val route = WordCategoryRoute(
        categoryType = WordCategoryType.MediaCollection,
        categoryId = "0"
    )

    WordCategoryScreen(
        uiState = uiState,
        route = route,
        playerState = PlayerStateProvider.EmptyPlayerState,
        onBack = {},
        onClickWord = {},
        onClickFile = {},
        onRepeatWordContexts = {}
    )
}