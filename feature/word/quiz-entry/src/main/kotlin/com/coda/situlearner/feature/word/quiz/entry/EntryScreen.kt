package com.coda.situlearner.feature.word.quiz.entry

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.QuizDueMode
import com.coda.situlearner.core.ui.util.asTimeText
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.feature.word.quiz.entry.model.QuizState
import com.coda.situlearner.feature.word.quiz.entry.model.QuizTaskByDay
import com.coda.situlearner.feature.word.quiz.entry.widget.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.LegendItem
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun EntryScreen(
    onBack: () -> Unit,
    onNavigateToMeaning: (QuizDueMode) -> Unit,
    onNavigateToTranslation: (QuizDueMode) -> Unit,
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
    onNavigateToMeaning: (QuizDueMode) -> Unit,
    onNavigateToTranslation: (QuizDueMode) -> Unit
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
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is UiState.Success -> {
                    ContentBoard(
                        uiState = uiState,
                        onNavigateToMeaning = { onNavigateToMeaning(uiState.quizDueMode) },
                        onNavigateToTranslation = { onNavigateToTranslation(uiState.quizDueMode) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentBoard(
    uiState: UiState.Success,
    onNavigateToMeaning: () -> Unit,
    onNavigateToTranslation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuizEntryButtons(
            meaningQuizState = uiState.meaningQuizState,
            translationQuizState = uiState.translationQuizState,
            hasChatbot = uiState.hasChatbot,
            onNavigateToMeaning = onNavigateToMeaning,
            onNavigateToTranslation = onNavigateToTranslation
        )

        QuizOngoingChart(
            tasks = uiState.tasks,
            showTranslationTask = uiState.hasChatbot,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun QuizEntryButtons(
    meaningQuizState: QuizState,
    translationQuizState: QuizState,
    hasChatbot: Boolean,
    onNavigateToMeaning: () -> Unit,
    onNavigateToTranslation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuizButtonWithReason(
            enabled = meaningQuizState is QuizState.NeedQuiz,
            textRes = coreR.string.core_ui_meaning_quiz,
            disabledReason = when (meaningQuizState) {
                QuizState.NoWord -> stringResource(R.string.word_quiz_entry_meaning_no_word)
                QuizState.NeedQuiz -> ""
                is QuizState.WaitUntil -> stringResource(
                    R.string.word_quiz_entry_next_quiz_time,
                    meaningQuizState.nextQuizDate.asTimeText()
                )
            },
            onClick = onNavigateToMeaning,
        )

        QuizButtonWithReason(
            enabled = translationQuizState is QuizState.NeedQuiz && hasChatbot,
            textRes = coreR.string.core_ui_translation_quiz,
            disabledReason = if (hasChatbot) {
                when (translationQuizState) {
                    QuizState.NoWord -> stringResource(R.string.word_quiz_entry_translation_no_proficient_word)
                    is QuizState.WaitUntil -> stringResource(
                        R.string.word_quiz_entry_next_quiz_time,
                        translationQuizState.nextQuizDate.asTimeText()
                    )

                    QuizState.NeedQuiz -> ""
                }
            } else {
                stringResource(R.string.word_quiz_entry_translation_no_chatbot)
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

        Text(
            text = if (enabled) "" else disabledReason,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@SuppressLint("LocalContextResourcesRead")
@Composable
private fun QuizOngoingChart(
    tasks: List<QuizTaskByDay>,
    showTranslationTask: Boolean,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val context = LocalContext.current
    LaunchedEffect(tasks, showTranslationTask) {
        modelProducer.runTransaction {
            columnSeries {
                series(y = tasks.map { it.numMeaning })
                if (showTranslationTask) series(y = tasks.map { it.numTranslation })
                extras {
                    it[LegendLabelKey] = buildSet {
                        add(
                            context.resources.getString(coreR.string.core_ui_meaning_quiz)
                        )
                        if (showTranslationTask) add(
                            context.resources.getString(coreR.string.core_ui_translation_quiz)
                        )
                    }
                }
            }
        }
    }

    ProvideVicoTheme(theme = rememberM3VicoTheme()) {
        QuizOngoingChart(
            tasks = tasks,
            modelProducer = modelProducer,
            showTranslationTask = showTranslationTask,
            modifier = modifier
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun QuizOngoingChart(
    tasks: List<QuizTaskByDay>,
    modelProducer: CartesianChartModelProducer,
    showTranslationTask: Boolean,
    modifier: Modifier = Modifier,
) {
    val bottomAxisFormatter = rememberBottomAxisFormatter(tasks)

    val colorScheme = rememberM3VicoTheme()

    val legendItemLabelComponent = rememberTextComponent(TextStyle(vicoTheme.textColor))

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnCollectionSpacing = if (showTranslationTask) 24.dp else 48.dp
            ),
            startAxis = VerticalAxis.rememberStart(
                itemPlacer = VerticalAxis.ItemPlacer.step({ 1.0 })
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomAxisFormatter
            ),
            marker = rememberMarker(
                { c, targets ->
                    val dayIndex = targets.firstOrNull()?.x?.toInt() ?: return@rememberMarker ""
                    val task = tasks.getOrNull(dayIndex) ?: return@rememberMarker ""

                    if (!showTranslationTask) return@rememberMarker task.numMeaning.toString()

                    // NOTE: this a workaround to get the index of which series is clicked;
                    // the order of series is defined in the initialization of columnSeries,
                    // which is [meaning, translation]
                    val seriesIndex = c.markerSeriesIndex ?: return@rememberMarker ""
                    if (seriesIndex == 0) task.numMeaning.toString() else task.numTranslation.toString()
                }
            ),
            legend = rememberHorizontalLegend(
                items = { extraStore ->
                    extraStore[LegendLabelKey].forEachIndexed { index, label ->
                        add(
                            LegendItem(
                                ShapeComponent(
                                    Fill(colorScheme.columnCartesianLayerColors[index]),
                                    CircleShape
                                ),
                                legendItemLabelComponent,
                                label,
                            )
                        )
                    }
                },
                padding = Insets(top = 16.dp),
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier.height(248.dp),
    )
}

private val LegendLabelKey = ExtraStore.Key<Set<String>>()

@Composable
private fun rememberBottomAxisFormatter(tasks: List<QuizTaskByDay>): CartesianValueFormatter {
    val context = LocalContext.current
    return remember(tasks) {
        CartesianValueFormatter { _, x, _ ->
            val task = tasks.getOrNull(x.toInt()) ?: return@CartesianValueFormatter ""
            formatAxisLabel(context, task)
        }
    }
}

private fun formatAxisLabel(context: Context, task: QuizTaskByDay): String {
    val dayIndex = task.dayIndex
    val date = task.date

    return when {
        dayIndex == 0 -> context.resources.getString(coreR.string.core_ui_today)
        dayIndex == 1 -> context.resources.getString(coreR.string.core_ui_tomorrow)
        dayIndex < 7 -> {
            when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> context.resources.getString(coreR.string.core_ui_monday_abbr)
                DayOfWeek.TUESDAY -> context.resources.getString(coreR.string.core_ui_tuesday_abbr)
                DayOfWeek.WEDNESDAY -> context.resources.getString(coreR.string.core_ui_wednesday_abbr)
                DayOfWeek.THURSDAY -> context.resources.getString(coreR.string.core_ui_thursday_abbr)
                DayOfWeek.FRIDAY -> context.resources.getString(coreR.string.core_ui_friday_abbr)
                DayOfWeek.SATURDAY -> context.resources.getString(coreR.string.core_ui_saturday_abbr)
                DayOfWeek.SUNDAY -> context.resources.getString(coreR.string.core_ui_sunday_abbr)
            }
        }

        else -> {
            "${date.month.number}-${date.day}"
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ContentBoardPreview() {
    val tasks = buildList {
        repeat(14) {
            add(
                QuizTaskByDay(
                    date = LocalDate(2026, 1, it + 1),
                    dayIndex = it,
                    numMeaningUnset = 1,
                    numMeaningBeginner = 2,
                    numMeaningIntermediate = 3,
                    numMeaningProficient = 14 - it,
                    numTranslationUnset = 5,
                    numTranslationBeginner = 6,
                    numTranslationIntermediate = 7,
                    numTranslationProficient = it,
                    numTranslationPredicted = 9
                )
            )
        }
    }

    ContentBoard(
        uiState = UiState.Success(
            meaningQuizState = QuizState.NeedQuiz,
            translationQuizState = QuizState.NeedQuiz,
            tasks = tasks,
            hasChatbot = true,
            quizDueMode = QuizDueMode.Now
        ),
        onNavigateToMeaning = {},
        onNavigateToTranslation = {},
        modifier = Modifier.fillMaxSize()
    )
}