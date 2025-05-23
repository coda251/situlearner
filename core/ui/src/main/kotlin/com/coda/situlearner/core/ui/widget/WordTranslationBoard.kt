package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coda.situlearner.core.model.data.mapper.asWordInfo
import com.coda.situlearner.core.model.infra.RemoteWordInfoState
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.core.model.infra.WordTranslation
import com.coda.situlearner.core.testing.data.wordsTestData

/**
 * @param onSelect passing an index pair in which the first indicates
 * the translation index and the second indicates the word info index
 * in that translation.
 */
@Composable
fun WordTranslationBoard(
    translations: List<WordTranslation>,
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
            translators = translations.map { it.translatorName },
            selectedTranslatorIndex = translationIndex,
            onSelect = {
                translationIndex = it
                onSelect(translationIndex to infoIndexes[translationIndex])
            },
            contentPadding = PaddingValues(horizontal = 28.dp) // 12.dp + 16.dp
        )

        TranslationResultPanel(
            translation = translations[translationIndex],
            selectedInfoIndex = infoIndexes[translationIndex],
            onSelectInfoIndex = {
                infoIndexes = infoIndexes.toMutableList().apply {
                    this[translationIndex] = it
                }
                onSelect(translationIndex to it)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        )
    }
}

@Composable
private fun TranslatorChipsPanel(
    translators: List<String>,
    selectedTranslatorIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        itemsIndexed(items = translators) { index, it ->
            FilterChip(
                onClick = { onSelect(index) },
                label = { Text(it) },
                selected = index == selectedTranslatorIndex,
            )
        }
    }
}

@Composable
private fun TranslationResultPanel(
    translation: WordTranslation,
    selectedInfoIndex: Int?,
    onSelectInfoIndex: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (val wordInfoState = translation.infoState) {
            RemoteWordInfoState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            RemoteWordInfoState.Empty -> {
                WordInfoEmptyItem(modifier = Modifier.align(Alignment.Center))
            }

            RemoteWordInfoState.Error -> {}
            is RemoteWordInfoState.Single -> {
                WordInfoDetailItem(wordInfo = wordInfoState.info)
            }

            is RemoteWordInfoState.Multiple -> {
                WordInfosPanel(
                    wordInfos = wordInfoState.infos,
                    selectedInfoIndex = selectedInfoIndex,
                    onSelect = onSelectInfoIndex
                )
            }
        }
    }
}

@Composable
private fun WordInfosPanel(
    wordInfos: List<WordInfo>,
    selectedInfoIndex: Int?,
    onSelect: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val currentSelectedInfo = selectedInfoIndex?.let { wordInfos.getOrNull(it) }

    if (currentSelectedInfo != null) {
        WordInfoDetailItem(
            wordInfo = currentSelectedInfo,
            modifier = modifier,
            onBack = { onSelect(null) }
        )
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier
        ) {
            itemsIndexed(items = wordInfos) { index, it ->
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
@Preview(showBackground = true)
private fun TranslationResultPanelPreview() {

    val translation = WordTranslation(
        translatorName = "",
        infoState = RemoteWordInfoState.Multiple(
            infos = wordsTestData.map { it.asWordInfo() }
        )
    )

    var selectedInfoIndex by remember {
        mutableStateOf<Int?>(null)
    }

    TranslationResultPanel(
        translation = translation,
        onSelectInfoIndex = { selectedInfoIndex = it },
        selectedInfoIndex = selectedInfoIndex,
        modifier = Modifier.height(300.dp)
    )
}