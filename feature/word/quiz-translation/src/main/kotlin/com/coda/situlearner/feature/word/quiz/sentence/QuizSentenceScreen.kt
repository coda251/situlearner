package com.coda.situlearner.feature.word.quiz.sentence

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatRole
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.feature.word.quiz.sentence.domain.ChatStatus
import com.coda.situlearner.feature.word.quiz.sentence.util.ExternalChatbot
import com.coda.situlearner.feature.word.quiz.sentence.util.launchExternalChatbot
import com.coda.situlearner.feature.word.quiz.translation.R
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun QuizSentenceScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    viewModel: QuizSentenceViewModel = koinViewModel()
) {
    val quizState by viewModel.quizState.collectAsStateWithLifecycle()
    val evaluateState by viewModel.evaluateState.collectAsStateWithLifecycle()

    QuizSentenceScreen(
        quizState = quizState,
        evaluateState = evaluateState,
        onBack = onBack,
        onSubmit = viewModel::submit,
        onRetry = viewModel::retry,
        onNextQuiz = viewModel::nextQuiz,
        onViewWord = onNavigateToWordDetail,
        onEvaluate = viewModel::evaluate
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizSentenceScreen(
    quizState: QuizUiState,
    evaluateState: EvaluateState,
    onBack: () -> Unit,
    onSubmit: (QuizUiState.Data, String) -> Unit,
    onRetry: (QuizUiState.Data) -> Unit,
    onNextQuiz: () -> Unit,
    onViewWord: (String) -> Unit,
    onEvaluate: (EvaluateState.Data, Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(onBack)
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .consumeWindowInsets(paddingValues)
        ) {
            when (quizState) {
                QuizUiState.Loading -> {}
                QuizUiState.NoWordError -> {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.word_quiz_sentence_screen_no_word_error)
                    )
                }

                QuizUiState.NoChatbotError -> {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.word_quiz_sentence_screen_no_chatbot_error)
                    )
                }

                is QuizUiState.Data -> {
                    ChatContentBoard(
                        quizState = quizState,
                        evaluateState = evaluateState,
                        onSubmit = { onSubmit(quizState, it) },
                        onRetry = { onRetry(quizState) },
                        onNextQuiz = onNextQuiz,
                        onViewWord = onViewWord,
                        onEvaluate = onEvaluate
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatContentBoard(
    quizState: QuizUiState.Data,
    evaluateState: EvaluateState,
    onSubmit: (String) -> Unit,
    onRetry: () -> Unit,
    onNextQuiz: () -> Unit,
    onViewWord: (String) -> Unit,
    onEvaluate: (EvaluateState.Data, Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items = quizState.displayedMessages) {
                MessageItem(message = it)
            }

            item {
                when (quizState.chatStatus) {
                    ChatStatus.Streaming, ChatStatus.Error -> MessageItem(
                        message = ChatMessage(
                            role = ChatRole.Bot,
                            content = quizState.partial
                        )
                    )

                    else -> {}
                }
            }
            item {
                when (quizState.chatStatus) {
                    ChatStatus.Connecting -> MessageLoading()
                    ChatStatus.Error -> MessageError(
                        quizState.error
                            ?: stringResource(R.string.word_quiz_translation_screen_chat_service_error),
                        onRetry
                    )

                    else -> {}
                }
            }
        }

        if (quizState.phase == QuizPhase.Question) {
            InputBar(onSubmit)
        }

        AnimatedVisibility(quizState.hasUserAnswer) {
            Column {
                AnimatedVisibility(
                    visible = evaluateState is EvaluateState.Data &&
                            evaluateState.phase != EvaluatePhase.Done
                ) {
                    if (evaluateState is EvaluateState.Data) {
                        Column {
                            Spacer(modifier = Modifier.height(32.dp))

                            EvaluateBoard(
                                evaluateState,
                                onEvaluate = onEvaluate,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    quizState.totalTokens?.let {
                        TokenCostInfo(
                            totalTokens = it,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    ExtraOptionsFAB(
                        state = quizState,
                        onNextQuiz = onNextQuiz,
                        onViewWord = onViewWord,
                        modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InputBar(
    onSubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        value = text,
        onValueChange = { text = it },
        trailingIcon = {
            IconButton(
                onClick = {
                    // NOTE: delay 200L to make sure the soft keyboard is first hidden and
                    //  avoid recompose immediately (the whole input bar is not visible).
                    //  In this case, the probability of the "soft keyboard flickering bug"
                    //  (see https://stackoverflow.com/questions/76901241)
                    //  could decrease to 5 out of 10 (if no delay is deployed, then it is 9 / 10)
                    //  in debug mode.
                    focusManager.clearFocus(force = true)
                    coroutineScope.launch {
                        delay(200L)
                        onSubmit(text)
                        text = ""
                    }
                },
            ) {
                Icon(
                    modifier = Modifier.rotate(90f),
                    painter = painterResource(coreR.drawable.arrow_back_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun TokenCostInfo(
    totalTokens: Int,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.word_quiz_translation_screen_token_cost, totalTokens),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ExtraOptionsFAB(
    state: QuizUiState.Data,
    onNextQuiz: () -> Unit,
    onViewWord: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current

    var showExtraOptions by remember { mutableStateOf(false) }

    // NOTE: wait for FloatingActionButtonMenu

    FloatingActionButton(
        modifier = modifier,
        onClick = { showExtraOptions = true }
    ) {
        Icon(
            painter = painterResource(coreR.drawable.add_24dp_000000_fill0_wght400_grad0_opsz24),
            contentDescription = null
        )

        DropdownMenu(
            expanded = showExtraOptions,
            onDismissRequest = { showExtraOptions = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(
                            R.string.word_quiz_sentence_screen_ask_external,
                            ExternalChatbot.ChatGPT.displayName
                        )
                    )
                },
                onClick = {
                    showExtraOptions = false
                    if (state.hasUserAnswer) {
                        clipboard.nativeClipboard.setPrimaryClip(
                            ClipData.newPlainText(
                                "text",
                                state.reviewTemplate.buildPrompt(
                                    word = state.word,
                                    question = state.question,
                                    answer = state.userAnswer
                                )
                            )
                        )
                        launchExternalChatbot(context, ExternalChatbot.ChatGPT)
                    }
                }
            )

            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.word_quiz_sentence_screen_next_question))
                },
                onClick = {
                    showExtraOptions = false
                    onNextQuiz()
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.word_quiz_sentence_screen_view_word)
                    )
                },
                onClick = {
                    showExtraOptions = false
                    onViewWord(state.word.id)
                }
            )
        }
    }
}

@Composable
private fun EvaluateBoard(
    state: EvaluateState.Data,
    onEvaluate: (EvaluateState.Data, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = when (state.phase) {
        EvaluatePhase.Existence -> stringResource(
            R.string.word_quiz_sentence_screen_evaluate_is_used,
            state.word.word
        )

        EvaluatePhase.Usage -> {
            stringResource(R.string.word_quiz_sentence_screen_evaluate_usage)
        }

        EvaluatePhase.Recall -> {
            stringResource(R.string.word_quiz_sentence_screen_evaluate_recall)
        }

        EvaluatePhase.Done -> ""
    }

    Card(modifier) {
        ListItem(
            headlineContent = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = text,
                    textAlign = TextAlign.Center,
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        ListItem(
            headlineContent = {
                YesNoButtonGroup(
                    onChoose = { onEvaluate(state, it) },
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}


@Composable
private fun YesNoButtonGroup(
    onChoose: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { onChoose(false) }) {
            Text(
                text = stringResource(R.string.word_quiz_sentence_screen_evaluate_no)
            )
        }

        Button(
            onClick = { onChoose(true) }
        ) {
            Text(text = stringResource(R.string.word_quiz_sentence_screen_evaluate_yes))
        }
    }
}

@Composable
private fun MessageLoading() {
    ListItem(
        headlineContent = {},
        leadingContent = {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
            )
        },
        colors = ListItemDefaults.colors(Color.Transparent)
    )
}

@Composable
private fun MessageError(
    message: String,
    onRetry: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        },
        trailingContent = {
            IconButton(
                onClick = onRetry,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    painter = painterResource(coreR.drawable.refresh_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            }
        },
        colors = ListItemDefaults.colors(Color.Transparent)
    )
}

@Composable
private fun MessageItem(message: ChatMessage) {
    if (message.content.isBlank()) return

    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        when (message.role) {
            ChatRole.Bot -> {
                MarkdownText(
                    modifier = Modifier.fillMaxWidth(),
                    markdown = message.content
                )
            }

            ChatRole.User -> {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Card {
                        MarkdownText(
                            modifier = Modifier.padding(12.dp),
                            markdown = message.content
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ChatMessagePreview() {
    Column {
        MessageItem(
            message = ChatMessage(
                role = ChatRole.Bot,
                content = "Hello, how are you?"
            )
        )
        MessageItem(
            message = ChatMessage(
                role = ChatRole.User,
                content = "Good. How are you?"
            )
        )
        MessageItem(
            message = ChatMessage(
                role = ChatRole.Bot,
                content = "I am pretty **pretty** pretty pretty pretty pretty good."
            )
        )
        MessageItem(
            message = ChatMessage(
                role = ChatRole.User,
                content = "Well, that is a very mature answer. And I am gonna tell you that..."
            )
        )

        MessageLoading()
        MessageError("Network error", onRetry = {})
    }
}

@Composable
@Preview(showBackground = true)
private fun EvaluateBoardPreview() {
    val state by remember {
        mutableStateOf(
            EvaluateState.Data(
                word = Word(
                    id = "",
                    word = "Good",
                    language = Language.English
                ),
                question = "",
                userAnswer = ""
            )
        )
    }

    EvaluateBoard(
        state,
        onEvaluate = { _, _ -> }
    )
}