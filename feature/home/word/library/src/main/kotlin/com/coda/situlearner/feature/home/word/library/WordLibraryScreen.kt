package com.coda.situlearner.feature.home.word.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.mapper.toWordCategoryList
import com.coda.situlearner.core.model.domain.TimeFrame
import com.coda.situlearner.core.model.domain.WordCategory
import com.coda.situlearner.core.model.domain.WordCategoryList
import com.coda.situlearner.core.model.domain.WordMediaCategory
import com.coda.situlearner.core.model.domain.WordPOSCategory
import com.coda.situlearner.core.model.domain.WordProficiencyCategory
import com.coda.situlearner.core.model.domain.WordViewedDateCategory
import com.coda.situlearner.core.model.domain.toTimeFrame
import com.coda.situlearner.core.testing.data.currentDateTestData
import com.coda.situlearner.core.testing.data.wordWithContextsListTestData
import com.coda.situlearner.core.ui.util.asText
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.ProficiencyIconSet
import com.coda.situlearner.core.ui.widget.WordContextText
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WordLibraryScreen(
    onNavigateToWordCategory: (String) -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WordLibraryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WordLibraryScreen(
        uiState = uiState,
        onClickCategory = { onNavigateToWordCategory(it.id) },
        onClickContextView = { onNavigateToWordDetail(it.wordContext.wordId) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordLibraryScreen(
    uiState: WordLibraryUiState,
    onClickCategory: (WordCategory) -> Unit,
    onClickContextView: (WordContextView) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSelectorBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.home_word_library_screen_title))
                },
                actions = {
                    IconButton(
                        onClick = { showSelectorBottomSheet = true }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.category_24dp_000000_fill0_wght0_grad0_opsz24),
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            when (uiState) {
                WordLibraryUiState.Empty -> {}

                WordLibraryUiState.Loading -> {}

                is WordLibraryUiState.Success -> WordCategoriesContentBoard(
                    categories = uiState.categories,
                    onClickCategory = onClickCategory,
                    onClickContextView = onClickContextView,
                )
            }
        }
    }

    if (showSelectorBottomSheet) {
        WordCategoriesSelectorBottomSheet(
            onDismiss = {
                showSelectorBottomSheet = false
            }
        )
    }
}

@Composable
private fun WordCategoriesContentBoard(
    categories: WordCategoryList,
    onClickCategory: (WordCategory) -> Unit,
    onClickContextView: (WordContextView) -> Unit,
    modifier: Modifier = Modifier
) {
    when (categories.categoryType) {
        WordCategoryType.LastViewedDate -> {
            WordViewedDateCategoriesBoard(
                categories = categories.asTypedCategoryList(),
                onClickCategory = onClickCategory,
                onClickContextView = onClickContextView,
                modifier = modifier
            )
        }

        WordCategoryType.Proficiency -> {
            WordProficiencyCategoriesBoard(
                categories = categories.asTypedCategoryList(),
                onClickCategory = onClickCategory,
                onClickContextView = onClickContextView
            )
        }

        WordCategoryType.PartOfSpeech -> {
            WordPOSCategoriesBoard(
                categories = categories.asTypedCategoryList(),
                onClickCategory = onClickCategory,
                onClickContextView = onClickContextView
            )
        }

        WordCategoryType.Media -> {
            WordMediaCategoriesBoard(
                categories = categories.asTypedCategoryList(),
                onClickCategory = onClickCategory,
                onClickContextView = onClickContextView
            )
        }
    }
}

@Composable
private fun WordViewedDateCategoriesBoard(
    categories: List<WordViewedDateCategory>,
    onClickCategory: (WordViewedDateCategory) -> Unit,
    onClickContextView: (WordContextView) -> Unit,
    modifier: Modifier = Modifier
) {
    CommonWordCategoriesBoard(
        categories = categories,
        onClickCategory = onClickCategory,
        onClickContextView = onClickContextView,
        modifier = modifier,
        headlineContent = {
            Text(
                text = it.timeFrame.asText(),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    )
}

@Composable
private fun WordProficiencyCategoriesBoard(
    categories: List<WordProficiencyCategory>,
    onClickCategory: (WordProficiencyCategory) -> Unit,
    onClickContextView: (WordContextView) -> Unit,
    modifier: Modifier = Modifier
) {
    CommonWordCategoriesBoard(
        categories = categories,
        onClickCategory = onClickCategory,
        onClickContextView = onClickContextView,
        modifier = modifier,
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = it.proficiency.asText(),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                ProficiencyIconSet(
                    proficiency = it.proficiency
                )
            }
        }
    )
}

@Composable
private fun WordPOSCategoriesBoard(
    categories: List<WordPOSCategory>,
    onClickCategory: (WordPOSCategory) -> Unit,
    onClickContextView: (WordContextView) -> Unit,
    modifier: Modifier = Modifier
) {
    CommonWordCategoriesBoard(
        categories = categories,
        onClickCategory = onClickCategory,
        onClickContextView = onClickContextView,
        modifier = modifier,
        headlineContent = {
            Text(
                text = it.partOfSpeech.asText(),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    )
}

@Composable
private fun WordMediaCategoriesBoard(
    categories: List<WordMediaCategory>,
    onClickCategory: (WordMediaCategory) -> Unit,
    onClickContextView: (WordContextView) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    CommonWordCategoriesBoard(
        categories = categories,
        onClickCategory = onClickCategory,
        onClickContextView = onClickContextView,
        modifier = modifier,
        headlineContent = {
            Text(
                text = it.collection.name,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        trailingContent = {
            // measure one-line text height to determine the height of image
            val headlineLayoutResult = textMeasurer.measure(
                text = it.collection.name,
                style = MaterialTheme.typography.headlineMedium,
                constraints = Constraints(maxWidth = Int.MAX_VALUE)
            )
            val subtitleLayoutResult = textMeasurer.measure(
                text = "${it.wordCount} ${stringResource(R.string.home_word_library_screen_words)}",
                style = MaterialTheme.typography.bodyLarge,
                constraints = Constraints(maxWidth = Int.MAX_VALUE)
            )
            val totalHeightPx = headlineLayoutResult.size.height + subtitleLayoutResult.size.height
            val totalHeightDp = with(LocalDensity.current) { totalHeightPx.toDp() }

            AsyncMediaImage(
                model = it.collection.coverImageUrl,
                modifier = Modifier
                    .size(totalHeightDp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
            )
        }
    )
}

@Composable
private fun <T : WordCategory> CommonWordCategoriesBoard(
    categories: List<T>,
    onClickCategory: (T) -> Unit,
    onClickContextView: (WordContextView) -> Unit,
    headlineContent: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable ((T) -> Unit)? = null
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(
            items = categories,
            key = { it.hashCode() }
        ) {
            Card {
                ListItem(
                    modifier = Modifier.padding(vertical = 8.dp),
                    headlineContent = {
                        headlineContent(it)
                    },
                    supportingContent = {
                        Text(
                            text = "${it.wordCount} ${stringResource(R.string.home_word_library_screen_words)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    trailingContent = trailingContent?.let { content -> { content(it) } },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                WordCategoryCardContent(
                    category = it,
                    onClickCategory = { onClickCategory(it) },
                    onClickContextView = {
                        onClickContextView(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun <T : WordCategory> WordCategoryCardContent(
    category: T,
    onClickCategory: (T) -> Unit,
    onClickContextView: (WordContextView) -> Unit
) {
    var displayIndexOffset by remember {
        mutableIntStateOf(0)
    }
    val displayWindowSize = 2
    val displayItems by remember(displayIndexOffset, category) {
        derivedStateOf {
            category.wordContexts.drop(displayIndexOffset).take(displayWindowSize)
        }
    }

    AnimatedContent(
        targetState = displayItems,
        transitionSpec = {
            (slideInVertically { it } + fadeIn()).togetherWith(slideOutVertically { -it } + fadeOut())
        }, label = ""
    ) { it ->
        if (it.isEmpty()) {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.home_word_library_screen_no_word_contexts_available),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        } else {
            Column {
                it.forEach {
                    ListItem(
                        headlineContent = {
                            WordContextText(it.wordContext)
                        },
                        trailingContent = {
                            IconButton(onClick = {
                                onClickContextView(it)
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_forward_24dp_000000_fill0_wght400_grad0_opsz24),
                                    contentDescription = null
                                )
                            }
                        },
                        supportingContent = it.mediaFile?.let {
                            { Text(it.name) }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.clickable { onClickContextView(it) }
                    )
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        if (category.wordContexts.size > displayWindowSize) {
            IconButton(
                modifier = Modifier.offset(x = (-12).dp),
                onClick = {
                    displayIndexOffset += displayWindowSize
                    if (displayIndexOffset >= category.wordContexts.size) {
                        displayIndexOffset = 0
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.refresh_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { onClickCategory(category) }) {
            Text(text = stringResource(R.string.home_word_library_screen_more))
        }
    }
}

private val WordCategory.id: String
    get() = when (this) {
        is WordViewedDateCategory -> this.timeFrame.name
        is WordProficiencyCategory -> this.proficiency.name
        is WordPOSCategory -> this.partOfSpeech.name
        is WordMediaCategory -> this.collection.id
    }

@Composable
private fun TimeFrame.asText(): String = when (this) {
    TimeFrame.Never -> stringResource(R.string.home_word_library_screen_time_frame_never)
    TimeFrame.Today -> stringResource(R.string.home_word_library_screen_time_frame_today)
    TimeFrame.LastThreeDays -> stringResource(R.string.home_word_library_screen_time_frame_last_three_days)
    TimeFrame.LastWeek -> stringResource(R.string.home_word_library_screen_time_frame_last_week)
    TimeFrame.LastTwoWeeks -> stringResource(R.string.home_word_library_screen_time_frame_last_two_weeks)
    TimeFrame.LastMonth -> stringResource(R.string.home_word_library_screen_time_frame_last_month)
    TimeFrame.OverAMonth -> stringResource(R.string.home_word_library_screen_time_frame_over_a_month)
}

@Composable
private fun WordProficiency.asText(): String = when (this) {
    WordProficiency.Unset -> stringResource(R.string.home_word_library_screen_proficiency_unset)
    WordProficiency.Beginner -> stringResource(R.string.home_word_library_screen_proficiency_beginner)
    WordProficiency.Intermediate -> stringResource(R.string.home_word_library_screen_proficiency_intermediate)
    WordProficiency.Proficient -> stringResource(R.string.home_word_library_screen_proficiency_proficient)
}

@Preview
@Composable
private fun WordLibraryScreenPreview() {

    val categoryType = WordCategoryType.LastViewedDate

    val uiState by remember {
        derivedStateOf {
            WordLibraryUiState.Success(
                categories = WordCategoryList(
                    categoryType = categoryType,
                    categories = wordWithContextsListTestData.filter { it.word.language == Language.English }
                        .toWordCategoryList(
                            categoryType
                        ) { instant ->
                            instant?.toTimeFrame(currentDateTestData) ?: TimeFrame.Never
                        }
                )
            )
        }
    }

    WordLibraryScreen(
        uiState = uiState,
        onClickCategory = {},
        onClickContextView = {},
    )
}