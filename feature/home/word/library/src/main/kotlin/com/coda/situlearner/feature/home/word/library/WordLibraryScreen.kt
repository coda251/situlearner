package com.coda.situlearner.feature.home.word.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.testing.data.wordWithContextsListTestData
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.WordContextText
import com.coda.situlearner.feature.home.word.library.model.WordBook
import com.coda.situlearner.feature.home.word.library.model.WordBookType
import com.coda.situlearner.feature.home.word.library.model.toWordBooks
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WordLibraryScreen(
    onNavigateToWordCategory: (WordCategoryType, String) -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WordLibraryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WordLibraryScreen(
        uiState = uiState,
        onClickBook = {
            onNavigateToWordCategory(
                when (it.type) {
                    WordBookType.All -> WordCategoryType.All
                    WordBookType.MediaCollection -> WordCategoryType.MediaCollection
                },
                it.id
            )
        },
        onClickContextView = { onNavigateToWordDetail(it.wordContext.wordId) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordLibraryScreen(
    uiState: WordLibraryUiState,
    onClickBook: (WordBook) -> Unit,
    onClickContextView: (WordContextView) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.home_word_library_screen_title))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                WordLibraryUiState.Empty -> {}

                WordLibraryUiState.Loading -> {}

                is WordLibraryUiState.Success -> WordLibraryContentBoard(
                    books = uiState.books,
                    wordContexts = uiState.wordContexts,
                    onClickBook = onClickBook,
                    onClickContextView = onClickContextView,
                )
            }
        }
    }
}

@Composable
private fun WordLibraryContentBoard(
    books: List<WordBook>,
    wordContexts: List<WordContextView>,
    onClickBook: (WordBook) -> Unit,
    onClickContextView: (WordContextView) -> Unit,
) {
    Column {
        WordBooksBoard(
            books = books,
            onClickBook = onClickBook,
        )

        WordRecommendationBoard(
            wordContexts = wordContexts,
            onClickContextView = onClickContextView,
        )
    }
}

@Composable
private fun WordBooksBoard(
    books: List<WordBook>,
    onClickBook: (WordBook) -> Unit,
) {
    Column {
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.home_word_library_screen_word_books)) },
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = books,
                key = { it.type.name + it.id }
            ) {
                WordBookItem(
                    book = it,
                    onClickBook = onClickBook
                )
            }
        }
    }
}

@Composable
private fun WordBookItem(
    book: WordBook,
    onClickBook: (WordBook) -> Unit,
) {
    Column(modifier = Modifier.width(150.dp)) {
        when (book.type) {
            WordBookType.All ->
                Icon(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onClickBook(book) },
                    painter = painterResource(R.drawable.dictionary_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )

            WordBookType.MediaCollection ->
                AsyncMediaImage(
                    model = book.coverUrl,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onClickBook(book) },
                )
        }

        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = when (book.type) {
                    WordBookType.All -> stringResource(R.string.home_word_library_screen_all_words)
                    WordBookType.MediaCollection -> book.name
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${book.wordCount} ${stringResource(R.string.home_word_library_screen_words)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun WordRecommendationBoard(
    wordContexts: List<WordContextView>,
    onClickContextView: (WordContextView) -> Unit,
) {
    Column {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.home_word_library_screen_word_recommendation)
                )
            }
        )

        Card(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            WordsRecommendationContent(
                wordContexts = wordContexts,
                onClickContextView = onClickContextView,
            )
        }
    }
}

@Composable
private fun WordsRecommendationContent(
    wordContexts: List<WordContextView>,
    onClickContextView: (WordContextView) -> Unit,
    displayWindowSize: Int = 2,
) {
    var displayIndexOffset by remember {
        mutableIntStateOf(0)
    }

    val displayItems by remember(displayIndexOffset, wordContexts) {
        derivedStateOf {
            wordContexts.drop(displayIndexOffset).take(displayWindowSize)
        }
    }

    AnimatedContent(
        targetState = displayItems,
        transitionSpec = {
            (slideInVertically { it } + fadeIn()).togetherWith(slideOutVertically { -it } + fadeOut())
        }, label = ""
    ) { it ->
        if (it.isEmpty()) {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.home_word_library_screen_no_word_contexts_available),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        } else {
            Column {
                it.forEach {
                    ListItem(
                        headlineContent = {
                            WordContextText(it.wordContext)
                        },
                        trailingContent = {
                            IconButton(onClick = {
                                onClickContextView(it)
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_forward_24dp_000000_fill0_wght400_grad0_opsz24),
                                    contentDescription = null
                                )
                            }
                        },
                        supportingContent = it.mediaFile?.let {
                            { Text(it.name) }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.clickable { onClickContextView(it) }
                    )
                }
            }
        }
    }

    if (wordContexts.size > displayWindowSize) {
        ListItem(
            headlineContent = {},
            trailingContent = {
                IconButton(
                    onClick = {
                        displayIndexOffset += displayWindowSize
                        if (displayIndexOffset >= wordContexts.size) {
                            displayIndexOffset = 0
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.refresh_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Preview
@Composable
private fun WordLibraryScreenPreview() {

    val uiState by remember {
        derivedStateOf {
            WordLibraryUiState.Success(
                books = wordWithContextsListTestData.toWordBooks(),
                wordContexts = wordWithContextsListTestData.flatMap { it.contexts }
            )
        }
    }

    WordLibraryScreen(
        uiState = uiState,
        onClickBook = {},
        onClickContextView = {},
    )
}