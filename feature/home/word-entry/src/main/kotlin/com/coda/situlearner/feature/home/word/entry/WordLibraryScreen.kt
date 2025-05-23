package com.coda.situlearner.feature.home.word.entry

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.core.testing.data.wordWithContextsListTestData
import com.coda.situlearner.core.ui.util.asText
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.LanguageSelectorDialog
import com.coda.situlearner.core.ui.widget.WordContextText
import com.coda.situlearner.feature.home.word.entry.model.WordBook
import com.coda.situlearner.feature.home.word.entry.model.WordBookType
import com.coda.situlearner.feature.home.word.entry.model.toWordBooks
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun WordLibraryScreen(
    onNavigateToWordBook: (String) -> Unit,
    onNavigateToWordList: (WordListType, String?) -> Unit,
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
            when (it.type) {
                WordBookType.All -> onNavigateToWordList(WordListType.All, null)
                WordBookType.NoMedia -> onNavigateToWordList(WordListType.NoMedia, null)
                WordBookType.MediaCollection -> onNavigateToWordBook(it.id)
            }
        },
        onClickContextView = { onNavigateToWordDetail(it.wordContext.wordId) },
        onClickRecommendations = { onNavigateToWordList(WordListType.Recommendation, null) },
        onQuiz = onNavigateToWordQuiz,
        onSetOffset = viewModel::setWordsOffset,
        onChangeLanguage = viewModel::setWordLibraryLanguage,
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
    onClickRecommendations: () -> Unit,
    onQuiz: () -> Unit,
    onSetOffset: (Int) -> Unit,
    onChangeLanguage: (Language) -> Unit,
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
                            IconButton(onClick = onQuiz) {
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
                is WordBooksUiState.Empty -> {
                    EmptyBoard(
                        language = booksUiState.language,
                        onChangeLanguage = onChangeLanguage
                    )
                }

                WordBooksUiState.Loading -> {}
                is WordBooksUiState.Success -> {
                    WordBooksBoard(
                        books = booksUiState.books,
                        onClickBook = onClickBook,
                    )
                }
            }

            when (wordsUiState) {
                RecommendedWordsUiState.Loading -> {}
                RecommendedWordsUiState.Empty -> {}
                is RecommendedWordsUiState.Success ->
                    WordRecommendationBoard(
                        wordContexts = wordsUiState.wordContexts,
                        offset = wordsUiState.offset,
                        onClickContextView = onClickContextView,
                        onSetOffset = onSetOffset,
                        onClickRecommendations = onClickRecommendations
                    )
            }
        }
    }
}

@Composable
private fun EmptyBoard(
    language: Language,
    onChangeLanguage: (Language) -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(
                R.string.home_word_library_screen_current_language_no_words,
                language.asText()
            )
        )
        TextButton(
            onClick = {
                showDialog = true
            }
        ) {
            Text(
                text = stringResource(R.string.home_word_library_screen_change_language)
            )
        }
    }

    if (showDialog) {
        LanguageSelectorDialog(
            choices = AppConfig.sourceLanguages,
            currentLanguage = language,
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false },
            onSelect = { onChangeLanguage(it) }
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

            else ->
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
                    WordBookType.NoMedia -> stringResource(R.string.home_word_library_screen_no_media)
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
    onClickRecommendations: () -> Unit,
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
                onSetOffset = onSetOffset,
                onClickRecommendations = onClickRecommendations
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
    onClickRecommendations: () -> Unit,
    displayWindowSize: Int = 2,
) {
    AnimatedContent(
        targetState = wordContexts.drop(offset).take(displayWindowSize),
        transitionSpec = {
            (slideInVertically { it } + fadeIn())
                .togetherWith(slideOutVertically { -it } + fadeOut())
        },
        label = ""
    ) {
        // contexts will not be empty logically
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

    if (wordContexts.size > displayWindowSize) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp, end = 16.dp, start = 8.dp)
        ) {
            IconButton(
                onClick = {
                    onSetOffset(
                        (offset + displayWindowSize).takeIf { it < wordContexts.size } ?: 0
                    )
                }
            ) {
                Icon(
                    painter = painterResource(coreR.drawable.refresh_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onClickRecommendations) {
                Text(text = stringResource(R.string.home_word_library_screen_more))
            }
        }
    }
}

@Preview
@Composable
private fun WordLibraryScreenContentPreview() {

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
        onClickRecommendations = {},
        onSetOffset = {
            wordsUiState = wordsUiState.copy(
                offset = if (it >= wordsUiState.wordContexts.size) 0 else it
            )
        },
        onChangeLanguage = {},
        onQuiz = {}
    )
}

@Preview
@Composable
private fun WordLibraryScreenEmptyPreview() {

    var booksUiState by remember {
        mutableStateOf<WordBooksUiState>(
            WordBooksUiState.Empty(Language.Japanese)
        )
    }

    WordLibraryScreen(
        booksUiState = booksUiState,
        wordsUiState = RecommendedWordsUiState.Empty,
        onClickBook = {},
        onClickContextView = {},
        onClickRecommendations = {},
        onSetOffset = {},
        onChangeLanguage = {
            booksUiState = when (it) {
                Language.English -> {
                    WordBooksUiState.Success(
                        books = wordWithContextsListTestData.toWordBooks(),
                    )
                }

                else -> {
                    WordBooksUiState.Empty(it)
                }
            }
        },
        onQuiz = {}
    )
}