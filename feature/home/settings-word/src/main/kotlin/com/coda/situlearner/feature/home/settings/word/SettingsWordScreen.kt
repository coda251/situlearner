package com.coda.situlearner.feature.home.settings.word

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.QuizDueMode
import com.coda.situlearner.core.model.data.TranslationEvalBackend
import com.coda.situlearner.core.model.data.WordBookSortBy
import com.coda.situlearner.core.ui.util.asText
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.NonEmptyTextInputDialog
import com.coda.situlearner.core.ui.widget.SingleChoiceSelector
import com.coda.situlearner.core.ui.widget.WordCountSelectorDialog
import org.koin.androidx.compose.koinViewModel

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
        onSetEvalBackend = viewModel::setTranslationEvalBackend,
        onSetQuizDueMode = viewModel::setQuizDueMode
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
    onSetEvalBackend: (TranslationEvalBackend) -> Unit,
    onSetQuizDueMode: (QuizDueMode) -> Unit,
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
                        quizDueMode = uiState.quizDueMode,
                        quizPromptTemplate = uiState.quizPromptTemplate,
                        evalPromptTemplate = uiState.evalPromptTemplate,
                        evalBackend = uiState.evalBackend,
                        onSetQuizWordCount = onSetQuizWordCount,
                        onSetQuizPromptTemplate = onSetQuizPromptTemplate,
                        onSetEvalPromptTemplate = onSetEvalPromptTemplate,
                        onSetEvalBackend = onSetEvalBackend,
                        onSelectWordLibraryLanguage = onSelectWordLibraryLanguage,
                        onSetRecommendedWordCount = onSetRecommendedWordCount,
                        onSetWordBookSortBy = onSetWordBookSortBy,
                        onSetQuizDueMode = onSetQuizDueMode
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
    quizDueMode: QuizDueMode,
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
    onSetQuizDueMode: (QuizDueMode) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Indicator(R.string.home_settings_word_screen_word_library_indicator)
        WordFilterLanguageSelector(wordLibraryLanguage, onSelectWordLibraryLanguage)
        RecommendedWordCountSelector(recommendedWordCount, onSetRecommendedWordCount)
        WordBookSortBySelector(wordBookSortBy, onSetWordBookSortBy)
        Indicator(R.string.home_settings_word_screen_word_quiz_indicator)
        QuizWordCountSelector(quizWordCount, onSetQuizWordCount)
        QuizDueModeSelector(quizDueMode, onSetQuizDueMode)
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
    SingleChoiceSelector(
        currentValue = language,
        choices = AppConfig.sourceLanguages,
        headline = stringResource(R.string.home_settings_word_screen_word_library_language),
        supportingText = language.asText(),
        valueToText = { it.asText() },
        onConfirm = onSelectLanguage
    )
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
    SingleChoiceSelector(
        currentValue = wordBookSortBy,
        choices = WordBookSortBy.entries,
        headline = stringResource(R.string.home_settings_word_screen_word_book_sort_by),
        supportingText = wordBookSortBy.asText(),
        valueToText = { it.asText() },
        onConfirm = onSelect
    )
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
private fun QuizDueModeSelector(
    quizDueMode: QuizDueMode,
    onSelect: (QuizDueMode) -> Unit
) {
    SingleChoiceSelector(
        currentValue = quizDueMode,
        choices = QuizDueMode.entries,
        headline = stringResource(R.string.home_settings_word_screen_quiz_due_mode),
        supportingText = quizDueMode.asText(),
        valueToText = { it.asText() },
        onConfirm = onSelect
    )
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
    SingleChoiceSelector(
        currentValue = evalBackend,
        choices = TranslationEvalBackend.entries,
        headline = stringResource(R.string.home_settings_word_screen_translation_eval_backend),
        supportingText = evalBackend.asText(),
        valueToText = { it.asText() },
        onConfirm = onSetEvalBackend
    )
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

@Composable
private fun QuizDueMode.asText() = when (this) {
    QuizDueMode.Now -> stringResource(R.string.home_settings_word_screen_quiz_due_mode_now)
    QuizDueMode.Today -> stringResource(R.string.home_settings_word_screen_quiz_due_mode_today)
}