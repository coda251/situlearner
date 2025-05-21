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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatRole
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.feature.word.quiz.sentence.util.ExternalChatbot
import com.coda.situlearner.feature.word.quiz.sentence.util.asExternalPrompt
import com.coda.situlearner.feature.word.quiz.sentence.util.launchExternalChatbot
import com.coda.situlearner.feature.word.quiz.translation.R
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
    val initUiState by viewModel.uiState.collectAsStateWithLifecycle()

    QuizSentenceScreen(
        uiState = initUiState,
        onBack = onBack,
        onSubmit = viewModel::submit,
        onRetry = viewModel::retry,
        onNextQuiz = viewModel::nextQuiz,
        onViewWord = onNavigateToWordDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizSentenceScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSubmit: (UiState.ChatSession, String) -> Unit,
    onRetry: (UiState.ChatSession) -> Unit,
    onNextQuiz: () -> Unit,
    onViewWord: (String) -> Unit
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
            when (uiState) {
                UiState.Loading -> {}
                UiState.NoWordError -> {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.word_quiz_sentence_screen_no_word_error)
                    )
                }

                UiState.NoChatbotError -> {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.word_quiz_sentence_screen_no_chatbot_error)
                    )
                }

                is UiState.ChatSession -> {
                    ChatScreen(
                        session = uiState,
                        onSubmit = { onSubmit(uiState, it) },
                        onRetry = { onRetry(uiState) },
                        onNextQuiz = onNextQuiz,
                        onViewWord = onViewWord
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatScreen(
    session: UiState.ChatSession,
    onSubmit: (String) -> Unit,
    onRetry: () -> Unit,
    onNextQuiz: () -> Unit,
    onViewWord: (String) -> Unit
) {
    when (session.quizState) {
        QuizState.LoadingQuestion -> {
            LoadingQuestionBoard(session, onRetry)
        }

        else -> {
            ChatContentBoard(
                session = session,
                onSubmit = onSubmit,
                onRetry = onRetry,
                onNextQuiz = onNextQuiz,
                onViewWord = onViewWord
            )
        }
    }
}

@Composable
private fun ChatContentBoard(
    session: UiState.ChatSession,
    onSubmit: (String) -> Unit,
    onRetry: () -> Unit,
    onNextQuiz: () -> Unit,
    onViewWord: (String) -> Unit,
) {
    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items = session.messages) {
                MessageItem(message = it)
            }
            item {
                when (val state = session.state) {
                    ChatSessionState.Loading -> MessageLoading()
                    is ChatSessionState.Error -> MessageError(state.message, onRetry)
                    ChatSessionState.WaitingInput -> {}
                }
            }
        }

        if (session.quizState == QuizState.Question) {
            InputBar(onSubmit)
        }

        AnimatedVisibility(
            visible = session.quizState == QuizState.LoadingAnswer ||
                    session.quizState == QuizState.Answer
        ) {
            ExtraOptionsFAB(session, onNextQuiz, onViewWord)
        }
    }
}

@Composable
private fun LoadingQuestionBoard(
    session: UiState.ChatSession,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = session.state) {
            ChatSessionState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is ChatSessionState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = onRetry) {
                        Text(
                            text = stringResource(R.string.word_quiz_sentence_screen_retry),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            ChatSessionState.WaitingInput -> {}
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
private fun ExtraOptionsFAB(
    session: UiState.ChatSession,
    onNextQuiz: () -> Unit,
    onViewWord: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current

    var showExtraOptions by remember { mutableStateOf(false) }

    // NOTE: wait for FloatingActionButtonMenu
    Box(modifier = Modifier.fillMaxWidth()) {
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick = {
                showExtraOptions = true
            }
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
                        clipboard.nativeClipboard.setPrimaryClip(
                            ClipData.newPlainText(
                                "text",
                                session.asExternalPrompt()
                            )
                        )
                        launchExternalChatbot(context, ExternalChatbot.ChatGPT)
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
                        onViewWord(session.word.id)
                    }
                )
            }
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
    ListItem(
        headlineContent = {
            when (message.role) {
                ChatRole.Bot -> {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = message.content
                    )
                }

                ChatRole.User -> {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(1f))
                        Card {
                            Text(
                                modifier = Modifier.padding(12.dp),
                                text = message.content
                            )
                        }
                    }
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
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
                content = "I am pretty pretty pretty pretty pretty pretty good."
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