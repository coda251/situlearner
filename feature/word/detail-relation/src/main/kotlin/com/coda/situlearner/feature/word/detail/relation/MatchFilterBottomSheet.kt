package com.coda.situlearner.feature.word.detail.relation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
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
import com.coda.situlearner.feature.word.detail.relation.model.MatchSimilarityType
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
internal fun MatchFilterBottomSheet(
    onDismiss: () -> Unit,
    viewModel: WordRelationViewModel = koinViewModel(),
) {
    val uiState by viewModel.filterUiState.collectAsStateWithLifecycle()

    MatchFilterBottomSheet(
        uiState = uiState,
        onDismiss = onDismiss,
        onSelectSimilarity = viewModel::setSimilarityType,
        onSetThreshold = viewModel::setThreshold
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchFilterBottomSheet(
    uiState: MatchFilterUiState,
    onDismiss: () -> Unit,
    onSelectSimilarity: (MatchSimilarityType) -> Unit,
    onSetThreshold: (Double) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = {}
    ) {
        MatchFilterOptionBoard(
            similarityType = uiState.similarityType,
            threshold = uiState.threshold,
            onSelectSimilarity = onSelectSimilarity,
            onSetThreshold = onSetThreshold
        )
    }
}

@Composable
private fun MatchFilterOptionBoard(
    similarityType: MatchSimilarityType,
    threshold: Double,
    onSelectSimilarity: (MatchSimilarityType) -> Unit,
    onSetThreshold: (Double) -> Unit,
) {
    Column {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.word_relation_screen_filter)
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        ListItem(
            headlineContent = {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        items = MatchSimilarityType.entries,
                        key = { it.ordinal }
                    ) {
                        FilterChip(
                            selected = it == similarityType,
                            onClick = { onSelectSimilarity(it) },
                            label = { Text(text = it.asText()) }
                        )
                    }
                }
            },
            leadingContent = {
                Text(
                    text = stringResource(R.string.word_relation_screen_similarity_type)
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        ListItem(
            headlineContent = {},
            leadingContent = {
                Text(
                    text = stringResource(R.string.word_relation_screen_similarity_threshold)
                )
            },
            trailingContent = {
                Text(text = String.format(Locale.getDefault(), "%.1f", threshold))
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        ListItem(
            headlineContent = {
                Slider(
                    value = threshold.toFloat(),
                    onValueChange = { onSetThreshold(it.toDouble()) },
                    valueRange = 0.2f..0.8f,
                    steps = 5
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun MatchSimilarityType.asText() = when (this) {
    MatchSimilarityType.Comprehensive -> stringResource(R.string.word_relation_screen_similarity_type_comprehensive)
    MatchSimilarityType.Pronunciation -> stringResource(R.string.word_relation_screen_similarity_type_pronunciation)
    MatchSimilarityType.Lemma -> stringResource(R.string.word_relation_screen_similarity_type_lemma)
}

@Composable
@Preview
private fun MatchFilterBottomSheetPreview() {
    var uiState by remember {
        mutableStateOf(
            MatchFilterUiState()
        )
    }

    MatchFilterBottomSheet(
        uiState = uiState,
        onDismiss = {},
        onSelectSimilarity = { uiState = uiState.copy(similarityType = it) },
        onSetThreshold = { uiState = uiState.copy(threshold = it) }
    )
}