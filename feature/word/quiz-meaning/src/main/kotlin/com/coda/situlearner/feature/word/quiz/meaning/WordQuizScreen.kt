package com.coda.situlearner.feature.word.quiz.meaning

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.mapper.asPlaylistItem
import com.coda.situlearner.core.testing.data.wordWithContextsListTestData
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.WordContextText
import com.coda.situlearner.core.model.feature.UserRating
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WordQuizScreen(
    onBack: () -> Unit,
    viewModel: WordQuizViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()

    WordQuizScreen(
        uiState = uiState,
        playerState = playerState,
        onBack = onBack,
        onRate = viewModel::onRate,
        onNext = viewModel::onNext
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordQuizScreen(
    uiState: WordQuizUiState,
    playerState: PlayerState,
    onBack: () -> Unit,
    onRate: (Word, UserRating) -> Unit,
    onNext: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    when (uiState) {
                        is WordQuizUiState.Success -> {
                            Text(text = "${(uiState.currentIndex + 1)} / ${uiState.words.size}")
                        }

                        is WordQuizUiState.Complete -> {
                            Text(text = stringResource(R.string.word_quiz_screen_quiz_result))
                        }

                        else -> {}
                    }
                },
                navigationIcon = {
                    BackButton(onBack)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            when (uiState) {
                WordQuizUiState.Loading -> {
                    InProgressBoard()
                }

                WordQuizUiState.Empty -> {
                    EmptyBoard()
                }

                is WordQuizUiState.Success -> {
                    QuizContentBoard(
                        words = uiState.words,
                        currentIndex = uiState.currentIndex,
                        playerState = playerState,
                        onRate = onRate,
                        onNext = onNext,
                    )
                }

                WordQuizUiState.Summarizing -> {
                    InProgressBoard()
                }

                is WordQuizUiState.Complete -> {
                    QuizCompleteBoard(uiState.result)
                }
            }
        }
    }
}

@Composable
private fun QuizCompleteBoard(
    result: Map<UserRating, Int>
) {
    Column {
        result.entries.sortedBy { it.key.level }.forEach {
            ListItem(
                headlineContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = it.key.asText())
                        Text(text = it.value.toString())
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyBoard() {
    Box(modifier = Modifier.fillMaxSize()) {
        ListItem(
            headlineContent = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.word_quiz_screen_no_words_to_quiz),
                    textAlign = TextAlign.Center
                )
            },
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun InProgressBoard() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun QuizContentBoard(
    words: List<Pair<Word, WordContextView?>>,
    currentIndex: Int,
    playerState: PlayerState,
    onRate: (Word, UserRating) -> Unit,
    onNext: (Int) -> Unit,
) {
    val state = rememberPagerState { words.size }

    HorizontalPager(
        state = state,
        userScrollEnabled = false,
    ) { index ->
        QuizItem(
            word = words[index].first,
            wordContext = words[index].second,
            isLastItem = index == words.lastIndex,
            playerState = playerState,
            onRate = onRate,
            onNext = { onNext(currentIndex) },
        )
    }

    LaunchedEffect(currentIndex) {
        state.animateScrollToPage(currentIndex)
    }
}

@Composable
private fun QuizItem(
    word: Word,
    wordContext: WordContextView?,
    isLastItem: Boolean,
    playerState: PlayerState,
    onRate: (Word, UserRating) -> Unit,
    onNext: () -> Unit,
) {
    var quizItemState by remember {
        mutableStateOf<QuizItemState>(QuizItemState.NoHint(wordContext))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f),
            // animated visibility do not work well with spaceBy, so instead we add spacer
            // for each component with animation
            verticalArrangement = Arrangement.Center,
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = word.word,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            )

            AnimatedVisibility(
                quizItemState is QuizItemState.HintWithContext
                        || quizItemState is QuizItemState.HintWithMedia
            ) {
                wordContext?.let {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        ListItem(
                            headlineContent = {
                                WordContextText(it.wordContext)
                            },
                            supportingContent = {
                                Text(text = it.mediaFile?.name ?: "")
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        )
                    }
                }
            }

            AnimatedVisibility(quizItemState is QuizItemState.HintWithMedia) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    PlayerViewCard(
                        playerState = playerState,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            AnimatedVisibility(quizItemState is QuizItemState.Answer) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    WordAnswerCard(word = word, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }

        QuizSelector(
            quizItemState = quizItemState,
            isLastItem = isLastItem,
            onRate = {
                onRate(word, it)
                playerState.clear()
                quizItemState = QuizItemState.Answer
            },
            onNext = onNext,
            onHint = {
                if (it is QuizItemState.HintWithMedia) {
                    it.context.asPlaylistItem()?.let { item ->
                        playerState.addItems(listOf(item), item)
                        playerState.play()
                    }
                }
                quizItemState = it
            }
        )
    }
}

@Composable
private fun WordAnswerCard(
    word: Word,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        word.pronunciation.takeIf { !it.isNullOrEmpty() }?.let {
            ListItem(
                headlineContent = {
                    Text(it)
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        LazyColumn {
            items(
                items = word.meanings,
                key = { it.partOfSpeechTag }
            ) {
                ListItem(
                    headlineContent = {
                        Text(text = it.definition)
                    },
                    overlineContent = {
                        Text(text = it.partOfSpeechTag)
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun PlayerViewCard(
    playerState: PlayerState,
    modifier: Modifier = Modifier
) {
    val isPlaying by playerState.isPlaying.collectAsStateWithLifecycle()

    val playlist by playerState.playlist.collectAsStateWithLifecycle()

    playlist.currentItem?.let {

        when (it.mediaType) {
            MediaType.Video -> {
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .clickable {
                            if (isPlaying) playerState.pause()
                            else playerState.play()
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    playerState.VideoOutput(modifier = Modifier)
                }
            }

            MediaType.Audio -> {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncMediaImage(
                        model = it.thumbnailUrl,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .align(Alignment.Center)
                            .clickable {
                                if (isPlaying) playerState.pause()
                                else playerState.play()
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizSelector(
    quizItemState: QuizItemState,
    isLastItem: Boolean,
    onHint: (QuizItemState) -> Unit,
    onRate: (UserRating) -> Unit,
    onNext: () -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (quizItemState !is QuizItemState.Answer) {
                val nextState = quizItemState.next()
                val hasMoreHint = nextState !is QuizItemState.Answer

                TextButton(
                    onClick = {
                        if (hasMoreHint) onHint(nextState)
                        else onRate(UserRating.Again)
                    }
                ) {
                    Text(
                        text = if (hasMoreHint) stringResource(R.string.word_quiz_screen_hint)
                        else stringResource(R.string.word_quiz_screen_do_not_know)
                    )
                }
            }

            Button(
                onClick = {
                    when (quizItemState) {
                        is QuizItemState.NoHint -> onRate(UserRating.Easy)
                        is QuizItemState.HintWithContext -> onRate(UserRating.Good)
                        is QuizItemState.HintWithMedia -> onRate(UserRating.Hard)
                        QuizItemState.Answer -> onNext()
                    }
                }
            ) {
                Text(
                    text = when (quizItemState) {
                        QuizItemState.Answer ->
                            if (isLastItem) stringResource(R.string.word_quiz_screen_done)
                            else stringResource(R.string.word_quiz_screen_next)

                        else -> stringResource(R.string.word_quiz_screen_know)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

private sealed interface QuizItemState {
    data class NoHint(val context: WordContextView?) : QuizItemState
    data class HintWithContext(val context: WordContextView) : QuizItemState
    data class HintWithMedia(val context: WordContextView) : QuizItemState
    data object Answer : QuizItemState

    fun next(): QuizItemState = when (this) {
        is NoHint -> context?.let { HintWithContext(it) } ?: Answer
        is HintWithContext -> if (context.mediaFile != null) HintWithMedia(context) else Answer
        is HintWithMedia -> Answer
        is Answer -> Answer
    }
}

@Composable
private fun UserRating.asText() = when (this) {
    UserRating.Again -> stringResource(R.string.word_quiz_screen_rating_again)
    UserRating.Hard -> stringResource(R.string.word_quiz_screen_rating_hard)
    UserRating.Good -> stringResource(R.string.word_quiz_screen_rating_good)
    UserRating.Easy -> stringResource(R.string.word_quiz_screen_rating_easy)
}

@Preview
@Composable
private fun QuizContentScreenPreview() {
    var uiState by remember {
        mutableStateOf(
            WordQuizUiState.Success(
                words = wordWithContextsListTestData.shuffled().map { wordWithContexts ->
                    wordWithContexts.word to wordWithContexts.contexts.run {
                        if (isEmpty()) null
                        else filter { it.mediaFile != null }.randomOrNull() ?: random()
                    }
                },
                currentIndex = 0
            )
        )
    }

    WordQuizScreen(
        uiState = uiState,
        playerState = PlayerStateProvider.EmptyPlayerState,
        onBack = {},
        onRate = { _, _ -> },
        onNext = {
            val nextIndex = it + 1
            uiState = if (nextIndex in uiState.words.indices) {
                uiState.copy(currentIndex = nextIndex)
            } else {
                uiState.copy(currentIndex = 0)
            }
        }
    )
}

@Preview
@Composable
private fun QuizCompleteScreenPreview() {
    val uiState = WordQuizUiState.Complete(
        result = mapOf(
            UserRating.Easy to 6,
            UserRating.Good to 2,
            UserRating.Hard to 5,
            UserRating.Again to 1
        )
    )

    WordQuizScreen(
        uiState = uiState,
        playerState = PlayerStateProvider.EmptyPlayerState,
        onBack = {},
        onRate = { _, _ -> },
        onNext = {}
    )
}