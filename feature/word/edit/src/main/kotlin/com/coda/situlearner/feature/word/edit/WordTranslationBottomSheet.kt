package com.coda.situlearner.feature.word.edit

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coda.situlearner.core.model.infra.RemoteWordInfoState
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.core.ui.widget.WordTranslationBoard
import com.coda.situlearner.core.ui.R as coreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WordTranslationBottomSheet(
    uiState: WordQueryUiState,
    onSelectWordInfo: (WordInfo?) -> Unit,
    onDismiss: () -> Unit,
) {
    if (uiState is WordQueryUiState.Result) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            dragHandle = null,
        ) {
            WordTranslationBoard(
                uiState = uiState,
                onConfirm = onSelectWordInfo,
                modifier = Modifier
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
}

@Composable
private fun WordTranslationBoard(
    uiState: WordQueryUiState.Result,
    onConfirm: (WordInfo?) -> Unit,
    modifier: Modifier = Modifier
) {
    var transIndexToInfoIndex by remember { mutableStateOf<Pair<Int, Int?>>(0 to null) }

    // NOTE: basically the same logic as in PlayerWordBottomSheet
    val displayedWordInfo =
        when (val infoState = uiState.translations[transIndexToInfoIndex.first].infoState) {
            is RemoteWordInfoState.Multiple -> {
                val infoIndex = transIndexToInfoIndex.second
                infoIndex?.let { infoState.infos.getOrNull(it) }?.let {
                    DisplayedWordInfoState.Result(it)
                } ?: DisplayedWordInfoState.ShouldBeSpecified
            }

            is RemoteWordInfoState.Single -> DisplayedWordInfoState.Result(infoState.info)
            else -> DisplayedWordInfoState.Empty
        }

    val externalWord = when (displayedWordInfo) {
        is DisplayedWordInfoState.Result -> displayedWordInfo.wordInfo.word.takeIf {
            it != uiState.queryWord
        }

        else -> ""
    }

    Column(modifier = modifier) {
        QueryWordItem(
            word = uiState.queryWord,
            externalWord = externalWord,
            onConfirm = {
                when (displayedWordInfo) {
                    DisplayedWordInfoState.Empty -> onConfirm(null)
                    // if no valid infoIndex is provided, then do nothing
                    DisplayedWordInfoState.ShouldBeSpecified -> {}
                    is DisplayedWordInfoState.Result -> onConfirm(displayedWordInfo.wordInfo)
                }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        WordTranslationBoard(
            translations = uiState.translations,
            onSelect = { transIndexToInfoIndex = it }
        )
    }
}

@Composable
private fun QueryWordItem(
    word: String,
    externalWord: String?,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
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
            TextButton(onConfirm) {
                Text(
                    text = stringResource(coreR.string.core_ui_ok)
                )
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