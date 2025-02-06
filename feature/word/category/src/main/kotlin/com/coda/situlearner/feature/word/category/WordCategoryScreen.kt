package com.coda.situlearner.feature.word.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.testing.data.wordsTestData
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.feature.word.category.util.formatInstant
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WordCategoryScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WordCategoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WordCategoryScreen(
        uiState = uiState,
        onBack = onBack,
        onClickWord = { onNavigateToWordDetail(it.id) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordCategoryScreen(
    uiState: WordCategoryUiState,
    onBack: () -> Unit,
    onClickWord: (Word) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onBack) },
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
                WordCategoryUiState.Error -> {}
                WordCategoryUiState.Empty -> {}
                WordCategoryUiState.Loading -> {}
                is WordCategoryUiState.Success -> {
                    WordCategoryContentBoard(
                        words = uiState.words,
                        onClickWord = onClickWord
                    )
                }
            }
        }
    }
}

@Composable
private fun WordCategoryContentBoard(
    words: List<Word>,
    onClickWord: (Word) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(
            items = words,
            key = { it.id }
        ) {
            WordItem(
                word = it,
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
                text = word.meanings?.firstOrNull()?.definition ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
        words = wordsTestData
    )
    WordCategoryScreen(
        uiState = uiState,
        onBack = {},
        onClickWord = {}
    )
}