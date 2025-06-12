package com.coda.situlearner.feature.home.settings.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.TranslationEvalBackend
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.NonEmptyTextInputDialog
import com.coda.situlearner.core.ui.widget.WordCountSelectorDialog
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SettingsQuizScreen(
    onBack: () -> Unit,
    viewModel: SettingsQuizViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsQuizScreen(
        uiState = uiState,
        onBack = onBack,
        onSetQuizWordCount = viewModel::setQuizWordCount,
        onSetQuizPromptTemplate = viewModel::setTranslationQuizPromptTemplate,
        onSetEvalPromptTemplate = viewModel::setTranslationEvalPromptTemplate,
        onSetEvalBackend = viewModel::setTranslationEvalBackend
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsQuizScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSetQuizWordCount: (UInt) -> Unit,
    onSetQuizPromptTemplate: (String) -> Unit,
    onSetEvalPromptTemplate: (String) -> Unit,
    onSetEvalBackend: (TranslationEvalBackend) -> Unit
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
                UiState.Loading -> {}
                is UiState.Success -> {
                    ContentBoard(
                        quizWordCount = uiState.quizWordCount,
                        quizPromptTemplate = uiState.quizPromptTemplate,
                        evalPromptTemplate = uiState.evalPromptTemplate,
                        evalBackend = uiState.evalBackend,
                        onSetQuizWordCount = onSetQuizWordCount,
                        onSetQuizPromptTemplate = onSetQuizPromptTemplate,
                        onSetEvalPromptTemplate = onSetEvalPromptTemplate,
                        onSetEvalBackend = onSetEvalBackend
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentBoard(
    quizWordCount: UInt,
    quizPromptTemplate: String,
    evalPromptTemplate: String,
    evalBackend: TranslationEvalBackend,
    onSetQuizWordCount: (UInt) -> Unit,
    onSetQuizPromptTemplate: (String) -> Unit,
    onSetEvalPromptTemplate: (String) -> Unit,
    onSetEvalBackend: (TranslationEvalBackend) -> Unit
) {
    Column {
        QuizWordCountSelector(quizWordCount, onSetQuizWordCount)
        QuizPromptTemplate(quizPromptTemplate, onSetQuizPromptTemplate)
        EvalPromptTemplate(evalPromptTemplate, onSetEvalPromptTemplate)
        EvalBackendSelector(evalBackend, onSetEvalBackend)
    }
}


@Composable
private fun QuizWordCountSelector(
    quizWordCount: UInt,
    onSetQuizWordCount: (UInt) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_quiz_screen_quiz_word_count)
            )
        },
        supportingContent = {
            Text(text = quizWordCount.toString())
        },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    if (showDialog) {
        WordCountSelectorDialog(
            initialCount = quizWordCount,
            valueRange = 5f..30f,
            steps = 4,
            onDismiss = {
                showDialog = false
            },
            onConfirm = {
                onSetQuizWordCount(it)
                showDialog = false
            }
        )
    }
}

@Composable
private fun QuizPromptTemplate(
    quizPromptTemplate: String,
    onSetQuizPromptTemplate: (String) -> Unit,
) {
    var quizPromptDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_quiz_screen_translation_quiz_prompt)
            )
        },
        supportingContent = {
            Text(stringResource(R.string.home_settings_quiz_screen_translation_quiz_prompt_desc))
        },
        modifier = Modifier.clickable {
            quizPromptDialog = true
        }
    )

    if (quizPromptDialog) {
        NonEmptyTextInputDialog(
            text = quizPromptTemplate,
            onDismiss = {
                quizPromptDialog = false
            },
            onConfirm = {
                onSetQuizPromptTemplate(it)
                quizPromptDialog = false
            }
        )
    }
}

@Composable
private fun EvalPromptTemplate(
    evalPromptTemplate: String,
    onSetEvalPromptTemplate: (String) -> Unit,
) {
    var evalPromptDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_quiz_screen_translation_eval_prompt)
            )
        },
        supportingContent = {
            Text(stringResource(R.string.home_settings_quiz_screen_translation_eval_prompt_desc))
        },
        modifier = Modifier.clickable {
            evalPromptDialog = true
        }
    )

    if (evalPromptDialog) {
        NonEmptyTextInputDialog(
            text = evalPromptTemplate,
            onDismiss = {
                evalPromptDialog = false
            },
            onConfirm = {
                onSetEvalPromptTemplate(it)
                evalPromptDialog = false
            }
        )
    }
}

@Composable
private fun EvalBackendSelector(
    evalBackend: TranslationEvalBackend,
    onSetEvalBackend: (TranslationEvalBackend) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_quiz_screen_translation_eval_backend)
            )
        },
        supportingContent = {
            Text(
                text = evalBackend.asText()
            )
        },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(text = stringResource(com.coda.situlearner.core.ui.R.string.core_ui_ok))
                }
            },
            text = {
                Column {
                    TranslationEvalBackend.entries.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = it == evalBackend,
                                onClick = { onSetEvalBackend(it) },
                            )
                            Text(text = it.asText())
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun TranslationEvalBackend.asText() = when (this) {
    TranslationEvalBackend.None -> stringResource(R.string.home_settings_quiz_screen_translation_eval_backend_none)
    TranslationEvalBackend.UseExternalChatbot -> stringResource(R.string.home_settings_quiz_screen_translation_eval_backend_external)
    TranslationEvalBackend.UseBuiltinChatbot -> stringResource(R.string.home_settings_quiz_screen_translation_eval_backend_builtin)
}