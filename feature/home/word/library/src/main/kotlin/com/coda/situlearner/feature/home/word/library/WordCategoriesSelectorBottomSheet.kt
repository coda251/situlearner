package com.coda.situlearner.feature.home.word.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.core.ui.widget.LanguageChips
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WordCategoriesSelectorBottomSheet(
    onDismiss: () -> Unit,
    viewModel: WordCategoriesSelectorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WordCategoriesSelectorBottomSheet(
        uiState = uiState,
        onDismiss = onDismiss,
        onSelectFilterLanguage = viewModel::setWordFilterLanguage,
        onSelectCategoryType = viewModel::setWordCategoryType,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordCategoriesSelectorBottomSheet(
    uiState: WordCategoriesSelectorUiState,
    onDismiss: () -> Unit,
    onSelectFilterLanguage: (Language) -> Unit,
    onSelectCategoryType: (WordCategoryType) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = {}
    ) {
        when (uiState) {
            WordCategoriesSelectorUiState.Loading -> {}
            is WordCategoriesSelectorUiState.Success -> {
                WordCategoriesSelectorBoard(
                    selectedLanguage = uiState.filterLanguage,
                    selectedCategoryType = uiState.categoryType,
                    languageChoices = uiState.languageChoices,
                    onSelectLanguage = onSelectFilterLanguage,
                    onSelectCategoryType = onSelectCategoryType,
                )
            }
        }
    }
}

@Composable
private fun WordCategoriesSelectorBoard(
    selectedLanguage: Language,
    selectedCategoryType: WordCategoryType,
    languageChoices: List<Language>,
    onSelectCategoryType: (WordCategoryType) -> Unit,
    onSelectLanguage: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = WordCategoryType.entries.indexOfFirst { it == selectedCategoryType },
    )

    Column(modifier = modifier) {
        LanguageChips(
            selectedLanguage = selectedLanguage,
            languageChoices = languageChoices,
            onSelectLanguage = onSelectLanguage,
        )

        ListItem(
            headlineContent = {
                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = WordCategoryType.entries,
                        key = { it.name }
                    ) {
                        FilterChip(
                            selected = it == selectedCategoryType,
                            onClick = {
                                onSelectCategoryType(it)
                            },
                            label = {
                                Text(text = it.asText())
                            }
                        )
                    }
                }
            },
            leadingContent = {
                Text(
                    text = stringResource(R.string.home_word_library_screen_category_type),
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun WordCategoryType.asText(): String = when (this) {
    WordCategoryType.LastViewedDate -> stringResource(R.string.home_word_library_screen_category_type_by_date)
    WordCategoryType.Proficiency -> stringResource(R.string.home_word_library_screen_category_type_by_proficiency)
    WordCategoryType.Media -> stringResource(R.string.home_word_library_screen_category_type_by_media)
    WordCategoryType.PartOfSpeech -> stringResource(R.string.home_word_library_screen_category_type_by_pos)
}

@Preview(showBackground = true)
@Composable
private fun WordCategoriesSelectorBoardPreview() {
    var uiState by remember {
        mutableStateOf(
            WordCategoriesSelectorUiState.Success(
                languageChoices = listOf(Language.English, Language.Japanese),
                filterLanguage = Language.English,
                categoryType = WordCategoryType.Proficiency,
            )
        )
    }

    WordCategoriesSelectorBottomSheet(
        uiState = uiState,
        onDismiss = {},
        onSelectFilterLanguage = { uiState = uiState.copy(filterLanguage = it) },
        onSelectCategoryType = { uiState = uiState.copy(categoryType = it) }
    )
}