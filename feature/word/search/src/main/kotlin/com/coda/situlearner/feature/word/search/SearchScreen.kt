package com.coda.situlearner.feature.word.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.mapper.asPlaylistItem
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.WordContextText
import com.coda.situlearner.feature.word.search.model.SearchResult
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun SearchScreen(
    onBack: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val queryWord by viewModel.queryWord.collectAsStateWithLifecycle()
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()

    SearchScreen(
        uiState = uiState,
        playerState = playerState,
        queryWord = queryWord,
        onBack = onBack,
        onSearch = viewModel::search,
        onNavigateToPlayer = onNavigateToPlayer
    )
}

@Composable
private fun SearchScreen(
    uiState: UiState,
    playerState: PlayerState,
    queryWord: String,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onNavigateToPlayer: () -> Unit,
) {
    LaunchedEffect(Unit) {
        playerState.clear()
    }

    Scaffold(
        topBar = {
            SearchBar(
                queryWord = queryWord,
                onBack = onBack,
                onSearch = onSearch
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
                UiState.Idle -> {}
                UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                UiState.Empty -> {
                    EmptyBoard(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiState.Success -> {
                    ContentBoard(
                        results = uiState.results,
                        onClick = {
                            val item = (it.collection to it.file).asPlaylistItem()
                            playerState.addItems(listOf(item), item)
                            playerState.setPlaybackLoop(
                                it.subtitle.startTimeInMs,
                                it.subtitle.endTimeInMs
                            )
                            playerState.play()
                            onNavigateToPlayer()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    queryWord: String,
    onBack: () -> Unit,
    onSearch: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var query by remember(queryWord) {
        mutableStateOf(queryWord)
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(queryWord) {
        if (queryWord.isEmpty()) {
            delay(200)
            focusRequester.requestFocus()
        }
    }

    TopSearchBar(
        state = rememberSearchBarState(),
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.focusRequester(focusRequester),
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    focusManager.clearFocus()
                    onSearch(query)
                },
                expanded = true,
                onExpandedChange = {},
                leadingIcon = {
                    BackButton(onBack = {
                        focusManager.clearFocus()
                        onBack()
                    })
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onSearch(query)
                        }
                    ) {
                        Icon(
                            painter = painterResource(coreR.drawable.search_24dp_000000_fill0_wght400_grad0_opsz24),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    )
}


@Composable
private fun ContentBoard(
    results: List<SearchResult>,
    onClick: (SearchResult) -> Unit,
) {
    LazyColumn {
        items(
            items = results,
            key = { it.hashCode() }
        ) {
            ListItem(
                leadingContent = {
                    AsyncMediaImage(
                        model = it.collection.coverImageUrl,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                },
                headlineContent = {
                    WordContextText(
                        subtitleSourceText = it.subtitle.sourceText,
                        startIndex = it.start,
                        endIndex = it.end
                    )
                },
                supportingContent = {
                    Text(text = it.file.name)
                },
                modifier = Modifier.clickable { onClick(it) }
            )
        }
    }
}

@Composable
private fun EmptyBoard(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.word_search_screen_empty_result),
        modifier = modifier
    )
}

@Composable
@Preview
private fun PreviewScreen() {
    SearchScreen(
        uiState = UiState.Idle,
        playerState = PlayerStateProvider.EmptyPlayerState,
        queryWord = "",
        onBack = {},
        onSearch = {},
        onNavigateToPlayer = {}
    )
}