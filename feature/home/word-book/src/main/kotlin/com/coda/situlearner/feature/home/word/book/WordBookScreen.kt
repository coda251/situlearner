package com.coda.situlearner.feature.home.word.book

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.core.testing.data.wordWithContextsListTestData
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.feature.home.word.book.model.WordChapter
import com.coda.situlearner.feature.home.word.book.model.WordChapterType
import com.coda.situlearner.feature.home.word.book.model.toChapters
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WordBookScreen(
    onBack: () -> Unit,
    onNavigateToWordList: (WordListType, String, WordProficiencyType) -> Unit,
    viewModel: WordBookViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WordBookScreen(
        uiState = uiState,
        onBack = onBack,
        onClickChapter = {
            onNavigateToWordList(
                when (it.type) {
                    WordChapterType.MediaFile -> WordListType.MediaFile
                    WordChapterType.MediaCollection -> WordListType.MediaCollection
                },
                it.id,
                it.progressType
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordBookScreen(
    uiState: WordBookUiState,
    onBack: () -> Unit,
    onClickChapter: (WordChapter) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(onBack)
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                WordBookUiState.Loading -> {}
                WordBookUiState.Empty -> {}
                is WordBookUiState.Success -> {
                    ContentBoard(
                        chapters = uiState.chapters,
                        onClickChapter = onClickChapter
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentBoard(
    chapters: List<WordChapter>,
    onClickChapter: (WordChapter) -> Unit,
) {
    LazyColumn {
        items(
            items = chapters,
            key = { it.type.name + it.id }
        ) {
            ChapterItem(chapter = it, onClickChapter = onClickChapter)
        }
    }
}

@Composable
private fun ChapterItem(
    chapter: WordChapter,
    onClickChapter: (WordChapter) -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                text = chapter.name
            )
        },
        supportingContent = {
            val label = when (chapter.type) {
                WordChapterType.MediaCollection -> when (chapter.progressType) {
                    WordProficiencyType.Meaning -> stringResource(R.string.home_word_book_screen_meaning_progress)
                    WordProficiencyType.Translation -> stringResource(R.string.home_word_book_screen_translation_progress)
                }

                WordChapterType.MediaFile -> stringResource(R.string.home_word_book_screen_progress)
            }
            Text(text = "$label ${chapter.progress}%")
        },
        trailingContent = {
            Text(text = chapter.wordCount.toString())
        },
        modifier = Modifier.clickable { onClickChapter(chapter) }
    )
}

@Preview
@Composable
private fun WordBookScreenPreview() {
    val uiState by remember {
        mutableStateOf(
            WordBookUiState.Success(
                wordWithContextsListTestData.toChapters("0")
            )
        )
    }

    WordBookScreen(
        uiState = uiState,
        onBack = {},
        onClickChapter = {}
    )
}