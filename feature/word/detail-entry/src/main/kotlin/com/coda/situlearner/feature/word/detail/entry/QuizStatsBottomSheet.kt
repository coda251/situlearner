package com.coda.situlearner.feature.word.detail.entry

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.feature.mapper.toWordProficiency
import com.coda.situlearner.core.ui.util.asTimeText
import com.coda.situlearner.core.ui.widget.ProficiencyIconSet
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuizStatsBottomSheet(
    onDismiss: () -> Unit,
    viewModel: WordDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.quizStatsUiState.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null
    ) {
        QuizStatsBoard(uiState)
    }
}

@Composable
private fun QuizStatsBoard(
    uiState: QuizStatsUiState
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 150.dp)
    ) {
        when (uiState) {
            is QuizStatsUiState.Success -> {
                ContentBoard(
                    meaningQuizStats = uiState.meaningQuizStats,
                    translationQuizStats = uiState.translationQuizStats
                )
            }

            QuizStatsUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun ContentBoard(
    meaningQuizStats: MeaningQuizStats?,
    translationQuizStats: TranslationQuizStats?
) {
    Column {
        MeaningQuizStatsBoard(meaningQuizStats)
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
        )
        TranslationQuizStatsBoard(translationQuizStats)
    }
}

@Composable
private fun MeaningQuizStatsBoard(
    stats: MeaningQuizStats?
) {
    Column {
        QuizStatsTitleItem(R.string.word_detail_screen_meaning_quiz)
        QuizStatsProficiencyItem(stats?.toWordProficiency())
        QuizStatsItem(
            titleRes = R.string.word_detail_screen_next_quiz,
            value = stats?.nextQuizDate?.asTimeText()
        )
    }
}

@Composable
private fun TranslationQuizStatsBoard(
    stats: TranslationQuizStats?
) {
    Column {
        QuizStatsTitleItem(R.string.word_detail_screen_translation_quiz)
        QuizStatsProficiencyItem(stats?.toWordProficiency())
        QuizStatsItem(
            titleRes = R.string.word_detail_screen_next_quiz,
            value = stats?.nextQuizDate?.asTimeText()
        )
        QuizStatsItem(
            titleRes = R.string.word_detail_screen_quiz_question,
            value = stats?.lastQuestion
        )
        QuizStatsItem(
            titleRes = R.string.word_detail_screen_your_answer,
            value = stats?.userAnswer
        )
    }
}

@Composable
private fun QuizStatsTitleItem(
    @StringRes titleRes: Int,
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(titleRes),
                modifier = Modifier.alpha(0.5f),
                fontWeight = FontWeight.Bold
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun QuizStatsItem(
    @StringRes titleRes: Int,
    value: String?,
) {
    ListItem(
        leadingContent = {
            Text(text = stringResource(titleRes))
        },
        headlineContent = {
            Text(
                text = value ?: stringResource(R.string.word_detail_screen_no_data)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun QuizStatsProficiencyItem(
    value: WordProficiency?,
) {
    ListItem(
        leadingContent = {
            Text(text = stringResource(R.string.word_detail_screen_proficiency))
        },
        headlineContent = {
            if (value != null) {
                ProficiencyIconSet(value)
            } else {
                Text(text = stringResource(R.string.word_detail_screen_no_data))
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}