package com.coda.situlearner.feature.word.detail.relation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.testing.data.wordsTestData
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.LineSpacer
import com.coda.situlearner.core.ui.widget.WordItem
import com.coda.situlearner.feature.word.detail.relation.model.WordMatchResult
import com.coda.situlearner.feature.word.detail.relation.widget.MatchedWordItem
import com.coda.situlearner.feature.word.detail.relation.widget.lemmaAnnotated
import com.coda.situlearner.feature.word.detail.relation.widget.pronunciationAnnotated
import org.koin.compose.viewmodel.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun WordRelationScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    viewModel: WordRelationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WordRelationScreen(
        uiState = uiState,
        onBack = onBack,
        onNavigateToWordDetail = onNavigateToWordDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordRelationScreen(
    uiState: MatchResultUiState,
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
) {
    var showFilterBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(
                        onBack = onBack
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showFilterBottomSheet = true }
                    ) {
                        Icon(
                            painter = painterResource(coreR.drawable.filter_list_24dp_000000_fill0_wght400_grad0_opsz24),
                            contentDescription = null
                        )
                    }
                }
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
                MatchResultUiState.Empty -> {}
                MatchResultUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is MatchResultUiState.Success -> {
                    QueryWithMatchedBoard(
                        query = uiState.query,
                        matchedWords = uiState.words,
                        version = uiState.version,
                        onClickWord = onNavigateToWordDetail
                    )
                }
            }
        }
    }

    if (showFilterBottomSheet && uiState is MatchResultUiState.Success) {
        MatchFilterBottomSheet(
            onDismiss = {
                showFilterBottomSheet = false
            }
        )
    }
}

@Composable
private fun QueryWithMatchedBoard(
    query: Word,
    matchedWords: List<WordMatchResult>,
    version: Long,
    onClickWord: (String) -> Unit
) {
    Column {
        QueryWordSection(word = query)
        LineSpacer(modifier = Modifier.fillMaxWidth())
        MatchedWordSection(
            words = matchedWords,
            onClickWord = onClickWord,
            version = version
        )
    }
}

@Composable
private fun QueryWordSection(word: Word) {
    WordItem(
        word = word.word,
        pronunciation = word.pronunciation,
        definition = word.meanings.firstOrNull()?.definition,
    )
}

@Composable
private fun MatchedWordSection(
    words: List<WordMatchResult>,
    version: Long,
    onClickWord: (String) -> Unit,
) {
    LazyColumn {
        items(
            items = words,
            key = { "${version}_${it.id}" }
        ) {
            MatchedWordItem(
                word = it.lemmaAnnotated(),
                modifier = Modifier.clickable { onClickWord(it.id) },
                pronunciation = it.pronunciationAnnotated(),
                definition = it.definition,
            )
        }
    }
}

@Composable
@Preview
private fun WordRelationScreenPreview() {
    val uiState = MatchResultUiState.Success(
        query = wordsTestData[0],
        words = listOf(
            WordMatchResult(
                id = wordsTestData[1].id,
                lemma = wordsTestData[1].word,
                pronunciation = wordsTestData[1].pronunciation,
                definition = wordsTestData[1].meanings.firstOrNull()?.definition,
                similarity = 0.48,
                pronunciationSimilarity = 0.0,
                lemmaSimilarity = 0.8,
                matchedPronunciationEndIndex = -1,
                matchedPronunciationStartIndex = -1,
            ),
            WordMatchResult(
                id = wordsTestData[2].id,
                lemma = wordsTestData[2].word,
                pronunciation = wordsTestData[2].pronunciation,
                definition = wordsTestData[2].meanings.firstOrNull()?.definition,
                similarity = 0.32,
                pronunciationSimilarity = 0.8,
                lemmaSimilarity = 0.0,
                matchedPronunciationStartIndex = 0,
                matchedPronunciationEndIndex = 8,
            ),
        )
    )

    WordRelationScreen(
        uiState = uiState,
        onBack = {},
        onNavigateToWordDetail = {}
    )
}