package com.coda.situlearner.feature.home.settings.word

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.TranslationEvalBackend
import com.coda.situlearner.core.model.data.WordBookSortBy
import com.coda.situlearner.core.ui.util.asText
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.LanguageSelectorDialog
import com.coda.situlearner.core.ui.widget.NonEmptyTextInputDialog
import com.coda.situlearner.core.ui.widget.WordCountSelectorDialog
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun SettingsWordScreen(
    onBack: () -> Unit,
    viewModel: SettingsWordViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsWordScreen(
        uiState = uiState,
        onBack = onBack,
        onSelectWordLibraryLanguage = viewModel::setWordLibraryLanguage,
        onSetRecommendedWordCount = viewModel::setRecommendedWordCount,
        onSetWordBookSortBy = viewModel::setWordBookSortBy,
        onSetQuizWordCount = viewModel::setQuizWordCount,
        onSetQuizPromptTemplate = viewModel::setTranslationQuizPromptTemplate,
        onSetEvalPromptTemplate = viewModel::setTranslationEvalPromptTemplate,
        onSetEvalBackend = viewModel::setTranslationEvalBackend
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsWordScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSelectWordLibraryLanguage: (Language) -> Unit,
    onSetRecommendedWordCount: (UInt) -> Unit,
    onSetWordBookSortBy: (WordBookSortBy) -> Unit,
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
                        wordLibraryLanguage = uiState.wordLibraryLanguage,
                        recommendedWordCount = uiState.recommendedWordCount,
                        wordBookSortBy = uiState.wordBookSortBy,
                        quizWordCount = uiState.quizWordCount,
                        quizPromptTemplate = uiState.quizPromptTemplate,
                        evalPromptTemplate = uiState.evalPromptTemplate,
                        evalBackend = uiState.evalBackend,
                        onSetQuizWordCount = onSetQuizWordCount,
                        onSetQuizPromptTemplate = onSetQuizPromptTemplate,
                        onSetEvalPromptTemplate = onSetEvalPromptTemplate,
                        onSetEvalBackend = onSetEvalBackend,
                        onSelectWordLibraryLanguage = onSelectWordLibraryLanguage,
                        onSetRecommendedWordCount = onSetRecommendedWordCount,
                        onSetWordBookSortBy = onSetWordBookSortBy
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentBoard(
    wordLibraryLanguage: Language,
    recommendedWordCount: UInt,
    wordBookSortBy: WordBookSortBy,
    quizWordCount: UInt,
    quizPromptTemplate: String,
    evalPromptTemplate: String,
    evalBackend: TranslationEvalBackend,
    onSelectWordLibraryLanguage: (Language) -> Unit,
    onSetRecommendedWordCount: (UInt) -> Unit,
    onSetWordBookSortBy: (WordBookSortBy) -> Unit,
    onSetQuizWordCount: (UInt) -> Unit,
    onSetQuizPromptTemplate: (String) -> Unit,
    onSetEvalPromptTemplate: (String) -> Unit,
    onSetEvalBackend: (TranslationEvalBackend) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Indicator(R.string.home_settings_word_screen_word_library_indicator)
        WordFilterLanguageSelector(wordLibraryLanguage, onSelectWordLibraryLanguage)
        RecommendedWordCountSelector(recommendedWordCount, onSetRecommendedWordCount)
        WordBookSortBySelector(wordBookSortBy, onSetWordBookSortBy)
        Indicator(R.string.home_settings_word_screen_word_quiz_indicator)
        QuizWordCountSelector(quizWordCount, onSetQuizWordCount)
        QuizPromptTemplate(quizPromptTemplate, onSetQuizPromptTemplate)
        EvalPromptTemplate(evalPromptTemplate, onSetEvalPromptTemplate)
        EvalBackendSelector(evalBackend, onSetEvalBackend)
    }
}

@Composable
private fun WordFilterLanguageSelector(
    language: Language,
    onSelectLanguage: (Language) -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_word_screen_word_library_language)
            )
        },
        supportingContent = {
            Text(
                text = language.asText()
            )
        },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    if (showDialog) {
        LanguageSelectorDialog(
            choices = AppConfig.sourceLanguages,
            currentLanguage = language,
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false },
            onSelect = { onSelectLanguage(it) }
        )
    }
}

@Composable
private fun RecommendedWordCountSelector(
    recommendedWordCount: UInt,
    onSetRecommendedWordCount: (UInt) -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_word_screen_recommended_word_count)
            )
        },
        supportingContent = {
            Text(text = recommendedWordCount.toString())
        },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    if (showDialog) {
        WordCountSelectorDialog(
            initialCount = recommendedWordCount,
            valueRange = 10f..50f,
            steps = 3,
            onDismiss = {
                showDialog = false
            },
            onConfirm = {
                onSetRecommendedWordCount(it)
                showDialog = false
            }
        )
    }
}

@Composable
private fun WordBookSortBySelector(
    wordBookSortBy: WordBookSortBy,
    onSelect: (WordBookSortBy) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_word_screen_word_book_sort_by)
            )
        },
        supportingContent = {
            Text(
                text = wordBookSortBy.asText()
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
                    Text(text = stringResource(coreR.string.core_ui_ok))
                }
            },
            text = {
                Column {
                    WordBookSortBy.entries.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = it == wordBookSortBy,
                                onClick = { onSelect(it) },
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
private fun Indicator(id: Int) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(id),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    )
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
                text = stringResource(R.string.home_settings_word_screen_quiz_word_count)
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
                text = stringResource(R.string.home_settings_word_screen_translation_quiz_prompt)
            )
        },
        supportingContent = {
            Text(stringResource(R.string.home_settings_word_screen_translation_quiz_prompt_desc))
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
                text = stringResource(R.string.home_settings_word_screen_translation_eval_prompt)
            )
        },
        supportingContent = {
            Text(stringResource(R.string.home_settings_word_screen_translation_eval_prompt_desc))
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
                text = stringResource(R.string.home_settings_word_screen_translation_eval_backend)
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
                    Text(text = stringResource(coreR.string.core_ui_ok))
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
    TranslationEvalBackend.None -> stringResource(R.string.home_settings_word_screen_translation_eval_backend_none)
    TranslationEvalBackend.UseExternalChatbot -> stringResource(R.string.home_settings_word_screen_translation_eval_backend_external)
    TranslationEvalBackend.UseBuiltinChatbot -> stringResource(R.string.home_settings_word_screen_translation_eval_backend_builtin)
}

@Composable
private fun WordBookSortBy.asText() = when (this) {
    WordBookSortBy.Count -> stringResource(R.string.home_settings_word_screen_word_book_sort_by_count)
    WordBookSortBy.UpdatedDate -> stringResource(R.string.home_settings_word_screen_word_book_sort_by_updated_date)
}