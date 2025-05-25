package com.coda.situlearner.feature.word.quiz.entry

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.ui.util.asTimeText
import com.coda.situlearner.core.ui.widget.BackButton
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun EntryScreen(
    onBack: () -> Unit,
    onNavigateToMeaning: () -> Unit,
    onNavigateToTranslation: () -> Unit,
    viewModel: EntryViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EntryScreen(
        uiState = uiState,
        onBack = onBack,
        onNavigateToMeaning = onNavigateToMeaning,
        onNavigateToTranslation = onNavigateToTranslation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onNavigateToMeaning: () -> Unit,
    onNavigateToTranslation: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    BackButton(onBack = onBack)
                }
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
                UiState.Loading -> {

                }

                is UiState.Success -> {
                    ContentBoard(
                        meaningQuizState = uiState.meaningQuizState,
                        translationQuizState = uiState.translationQuizState,
                        onNavigateToMeaning = onNavigateToMeaning,
                        onNavigateToTranslation = onNavigateToTranslation
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentBoard(
    meaningQuizState: MeaningQuizState,
    translationQuizState: TranslationQuizState,
    onNavigateToMeaning: () -> Unit,
    onNavigateToTranslation: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuizButtonWithReason(
            enabled = meaningQuizState is MeaningQuizState.Success,
            textRes = coreR.string.core_ui_meaning_quiz,
            disabledReason = when (meaningQuizState) {
                MeaningQuizState.NoWord -> stringResource(R.string.word_quiz_entry_meaning_no_word)
                MeaningQuizState.Success -> ""
                is MeaningQuizState.WaitUntil -> stringResource(
                    R.string.word_quiz_entry_meaning_next_quiz_time,
                    meaningQuizState.nextQuizDate.asTimeText()
                )
            },
            onClick = onNavigateToMeaning,
        )

        QuizButtonWithReason(
            enabled = translationQuizState == TranslationQuizState.Success,
            textRes = coreR.string.core_ui_translation_quiz,
            disabledReason = when (translationQuizState) {
                TranslationQuizState.NoWord -> stringResource(R.string.word_quiz_entry_translation_no_proficient_word)
                TranslationQuizState.NoChatbot -> stringResource(R.string.word_quiz_entry_translation_no_chatbot)
                TranslationQuizState.Success -> ""
            },
            onClick = onNavigateToTranslation,
        )
    }
}

@Composable
private fun QuizButtonWithReason(
    enabled: Boolean,
    @StringRes textRes: Int,
    disabledReason: String,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onClick,
            enabled = enabled
        ) {
            Text(stringResource(textRes))
        }

        if (!enabled) {
            Text(
                text = disabledReason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}