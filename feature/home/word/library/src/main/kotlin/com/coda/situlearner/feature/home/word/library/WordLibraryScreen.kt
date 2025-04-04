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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
    onNavigateToWordQuiz: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WordLibraryViewModel = koinViewModel()
) {
    val booksUiState by viewModel.booksUiState.collectAsStateWithLifecycle()
    val wordsUiState by viewModel.wordsUiState.collectAsStateWithLifecycle()

    WordLibraryScreen(
        booksUiState = booksUiState,
        wordsUiState = wordsUiState,
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
        onQuiz = onNavigateToWordQuiz,
        onSetOffset = viewModel::setWordsOffset,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordLibraryScreen(
    booksUiState: WordBooksUiState,
    wordsUiState: RecommendedWordsUiState,
    onClickBook: (WordBook) -> Unit,
    onClickContextView: (WordContextView) -> Unit,
    onQuiz: () -> Unit,
    onSetOffset: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.home_word_library_screen_title))
                },
                actions = {
                    when (booksUiState) {
                        is WordBooksUiState.Success -> {
                            IconButton(
                                onClick = onQuiz
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.quiz_24dp_000000_fill0_wght400_grad0_opsz24),
                                    contentDescription = null
                                )
                            }
                        }

                        else -> {}
                    }
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
            when (booksUiState) {
                WordBooksUiState.Empty -> {}
                WordBooksUiState.Loading -> {}
                is WordBooksUiState.Success ->
                    WordBooksBoard(
                        books = booksUiState.books,
                        onClickBook = onClickBook,
                    )
            }

            when (wordsUiState) {
                RecommendedWordsUiState.Loading -> {}
                RecommendedWordsUiState.Empty -> {}
                is RecommendedWordsUiState.Success ->
                    WordRecommendationBoard(
                        wordContexts = wordsUiState.wordContexts,
                        offset = wordsUiState.offset,
                        onClickContextView = onClickContextView,
                        onSetOffset = onSetOffset
                    )
            }
        }
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
    offset: Int,
    onClickContextView: (WordContextView) -> Unit,
    onSetOffset: (Int) -> Unit,
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
                offset = offset,
                onClickContextView = onClickContextView,
                onSetOffset = onSetOffset
            )
        }
    }
}

@Composable
private fun WordsRecommendationContent(
    wordContexts: List<WordContextView>,
    offset: Int,
    onClickContextView: (WordContextView) -> Unit,
    onSetOffset: (Int) -> Unit,
    displayWindowSize: Int = 2,
) {
    AnimatedContent(
        targetState = wordContexts.drop(offset).take(displayWindowSize),
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
                        onSetOffset(
                            (offset + displayWindowSize).takeIf { it < wordContexts.size } ?: 0
                        )
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

    val booksUiState by remember {
        mutableStateOf(
            WordBooksUiState.Success(
                books = wordWithContextsListTestData.toWordBooks(),
            )
        )
    }

    var wordsUiState by remember {
        mutableStateOf(
            RecommendedWordsUiState.Success(
                wordContexts = wordWithContextsListTestData.flatMap { it.contexts },
                offset = 0
            )
        )
    }

    WordLibraryScreen(
        booksUiState = booksUiState,
        wordsUiState = wordsUiState,
        onClickBook = {},
        onClickContextView = {},
        onSetOffset = {
            wordsUiState = wordsUiState.copy(
                offset = if (it >= wordsUiState.wordContexts.size) 0 else it
            )
        },
        onQuiz = {},
    )
}