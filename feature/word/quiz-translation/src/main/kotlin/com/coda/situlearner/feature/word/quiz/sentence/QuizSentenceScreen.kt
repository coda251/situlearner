package com.coda.situlearner.feature.word.quiz.sentence

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatRole
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.feature.word.quiz.translation.R
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun QuizSentenceScreen(
    onBack: () -> Unit,
    viewModel: QuizSentenceViewModel = koinViewModel()
) {
    val initUiState by viewModel.initUiState.collectAsStateWithLifecycle()
    val sessionUiState by viewModel.sessionUiState.collectAsStateWithLifecycle()

    QuizSentenceScreen(
        initUiState = initUiState,
        sessionUiState = sessionUiState,
        onBack = onBack,
        onSubmit = viewModel::submit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizSentenceScreen(
    initUiState: InitUiState,
    sessionUiState: SessionUiState,
    onBack: () -> Unit,
    onSubmit: (String) -> Unit
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .imePadding()
                .consumeWindowInsets(it)
        ) {
            when (initUiState) {
                InitUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                InitUiState.NoWordError -> {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.word_quiz_sentence_screen_no_word_error)
                    )
                }

                InitUiState.NoChatbotError -> {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.word_quiz_sentence_screen_no_chatbot_error)
                    )
                }

                is InitUiState.Ready -> {
                    when (sessionUiState) {
                        SessionUiState.Loading -> CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )

                        is SessionUiState.Result -> ChatScreen(sessionUiState, onSubmit)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatScreen(
    session: SessionUiState.Result,
    onSubmit: (String) -> Unit
) {
    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(
                items = session.messages.subList(1, session.messages.size),
            ) {
                ChatMessageItem(message = it)
            }
            item {
                when (val state = session.state) {
                    ChatSessionState.Loading -> ChatStateLoading()
                    is ChatSessionState.Error -> ChatStateError(state.message)
                    ChatSessionState.WaitingInput -> {}
                }
            }
        }

        InputBar(ableToSubmit = session.state != ChatSessionState.Loading, onSubmit)
    }
}

@Composable
private fun InputBar(
    ableToSubmit: Boolean = true,
    onSubmit: (String) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        value = text,
        onValueChange = {
            text = it
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    onSubmit(text)
                    text = ""
                },
                enabled = ableToSubmit
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
private fun ChatStateLoading() {
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
private fun ChatStateError(
    message: String
) {
    ListItem(
        headlineContent = {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(coreR.drawable.error_24dp_000000_fill0_wght400_grad0_opsz24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        colors = ListItemDefaults.colors(Color.Transparent)
    )
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
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
        ChatMessageItem(
            message = ChatMessage(
                role = ChatRole.Bot,
                content = "Hello, how are you?"
            )
        )
        ChatMessageItem(
            message = ChatMessage(
                role = ChatRole.User,
                content = "Good. How are you?"
            )
        )
        ChatMessageItem(
            message = ChatMessage(
                role = ChatRole.Bot,
                content = "I am pretty pretty pretty pretty pretty pretty good."
            )
        )
        ChatMessageItem(
            message = ChatMessage(
                role = ChatRole.User,
                content = "Well, that is a very mature answer. And I am gonna tell you that..."
            )
        )

        ChatStateLoading()
        ChatStateError("Network error")
    }
}

@Composable
@Preview(showBackground = true)
private fun InputBarPreview() {
    InputBar(onSubmit = {})
}