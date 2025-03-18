package com.coda.situlearner.feature.word.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.feature.word.category.model.SortMode
import com.coda.situlearner.feature.word.category.model.WordSortBy
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WordOptionBottomSheet(
    onDismiss: () -> Unit,
    viewModel: WordCategoryViewModel = koinViewModel(),
) {
    val uiState by viewModel.wordOptionUiState.collectAsStateWithLifecycle()

    WordOptionBottomSheet(
        uiState = uiState,
        onDismiss = onDismiss,
        onSelectSortMode = viewModel::setWordSortMode,
        onSelectWordSortBy = viewModel::setWordSortBy
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordOptionBottomSheet(
    uiState: WordOptionUiState,
    onDismiss: () -> Unit,
    onSelectSortMode: (SortMode) -> Unit,
    onSelectWordSortBy: (WordSortBy) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = {}
    ) {
        when (uiState) {
            WordOptionUiState.Loading -> {}
            is WordOptionUiState.Success -> {
                WordSortOptionBoard(
                    sortMode = uiState.sortMode,
                    wordSortBy = uiState.wordSortBy,
                    onSelectSortMode = onSelectSortMode,
                    onSelectWordSortBy = onSelectWordSortBy
                )
            }
        }
    }
}

@Composable
private fun WordSortOptionBoard(
    sortMode: SortMode,
    wordSortBy: WordSortBy,
    onSelectSortMode: (SortMode) -> Unit,
    onSelectWordSortBy: (WordSortBy) -> Unit,
) {
    Column {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.word_category_screen_sort)
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        ListItem(
            headlineContent = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortMode.entries.forEach {
                        FilterChip(
                            selected = it == sortMode,
                            onClick = { onSelectSortMode(it) },
                            label = { Text(text = it.asText()) }
                        )
                    }
                }
            },
            leadingContent = {
                Text(
                    text = stringResource(R.string.word_category_screen_sort_mode)
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        ListItem(
            headlineContent = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WordSortBy.entries.forEach {
                        FilterChip(
                            selected = it == wordSortBy,
                            onClick = { onSelectWordSortBy(it) },
                            label = { Text(text = it.asText()) }
                        )
                    }
                }
            },
            leadingContent = {
                Text(
                    text = stringResource(R.string.word_category_screen_word_sort_by)
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun SortMode.asText() = when (this) {
    SortMode.Ascending -> stringResource(R.string.word_category_screen_sort_mode_ascending)
    SortMode.Descending -> stringResource(R.string.word_category_screen_sort_mode_descending)
}

@Composable
private fun WordSortBy.asText() = when (this) {
    WordSortBy.LastViewedDate -> stringResource(R.string.word_category_screen_word_sort_by_date)
    WordSortBy.Proficiency -> stringResource(R.string.word_category_screen_word_sort_by_proficiency)
}

@Composable
@Preview
private fun WordOptionBottomSheetPreview() {
    var uiState by remember {
        mutableStateOf(
            WordOptionUiState.Success(
                sortMode = SortMode.Ascending,
                wordSortBy = WordSortBy.LastViewedDate
            )
        )
    }

    WordOptionBottomSheet(
        uiState = uiState,
        onDismiss = {},
        onSelectSortMode = { uiState = uiState.copy(sortMode = it) },
        onSelectWordSortBy = { uiState = uiState.copy(wordSortBy = it) }
    )
}