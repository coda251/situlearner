package com.coda.situlearner.feature.player.entry

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.feature.player.entry.widgets.subtitle.CenterIndicator
import com.coda.situlearner.feature.player.entry.widgets.subtitle.InteractionTimer
import com.coda.situlearner.feature.player.entry.widgets.subtitle.SubtitleTextDefault
import com.coda.situlearner.feature.player.entry.widgets.subtitle.SubtitleTextItem
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerState.Companion.TIME_UNSET
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PlayerSubtitleBoard(
    playerState: PlayerState,
    activeSubtitleIndex: Int,
    activeTokenStartIndex: Int,
    onClickTokenInSubtitleContext: (Token, SubtitleContext) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerSubtitleViewModel = koinViewModel()
) {
    val subtitleUiState by viewModel.subtitleUiState.collectAsStateWithLifecycle()

    PlayerSubtitleBoard(
        playerState = playerState,
        subtitleUiState = subtitleUiState,
        activeSubtitleIndex = activeSubtitleIndex,
        activeTokenStartIndex = activeTokenStartIndex,
        onClickTokenInSubtitleContext = onClickTokenInSubtitleContext,
        modifier = modifier,
    )
}

@Composable
private fun PlayerSubtitleBoard(
    playerState: PlayerState,
    subtitleUiState: SubtitleUiState,
    activeSubtitleIndex: Int,
    activeTokenStartIndex: Int,
    onClickTokenInSubtitleContext: (Token, SubtitleContext) -> Unit,
    modifier: Modifier = Modifier
) {
    when (subtitleUiState) {
        SubtitleUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        SubtitleUiState.Empty -> {}

        is SubtitleUiState.Success -> {
            PlayerSubtitleBoard(
                subtitles = subtitleUiState.subtitles,
                playerState = playerState,
                activeSubtitleIndex = activeSubtitleIndex,
                activeTokenStartIndex = activeTokenStartIndex,
                onClickTokenInSubtitle = { index, token, subtitle ->
                    onClickTokenInSubtitleContext(
                        token, SubtitleContext(
                            index = index,
                            subtitle = subtitle,
                            mediaId = subtitleUiState.mediaId,
                            language = subtitleUiState.language
                        )
                    )
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun PlayerSubtitleBoard(
    subtitles: List<Subtitle>,
    playerState: PlayerState,
    activeSubtitleIndex: Int,
    activeTokenStartIndex: Int,
    onClickTokenInSubtitle: (Int, Token, Subtitle) -> Unit,
    modifier: Modifier = Modifier
) {
    val loop by playerState.loopInMs.collectAsStateWithLifecycle()
    val positionInMs by playerState.positionInMs.collectAsStateWithLifecycle()

    val playingSubtitleIndex by remember(subtitles) {
        derivedStateOf {
            val result = subtitles.binarySearch {
                compareValues(it.startTimeInMs, positionInMs)
            }
            if (result >= 0) result else -(result + 2)
        }
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val size = with(LocalDensity.current) {
            IntSize(
                width = maxWidth.roundToPx(),
                height = maxHeight.roundToPx()
            )
        }

        PlayerSubtitleBoard(
            subtitles = subtitles,
            playingSubtitleIndex = playingSubtitleIndex,
            activeSubtitleIndex = activeSubtitleIndex,
            activeTokenStartIndex = activeTokenStartIndex,
            loop = loop,
            pageSize = size,
            onClickToken = { index, token ->
                onClickTokenInSubtitle(index, token, subtitles[index])
            },
            onClickSubtitle = { playerState.seekTo(it.startTimeInMs) },
            onDoubleClickSubtitle = {
                if (loop.first == it.startTimeInMs && loop.second == it.endTimeInMs) {
                    playerState.setPlaybackLoop(
                        TIME_UNSET,
                        TIME_UNSET
                    )
                } else {
                    playerState.setPlaybackLoop(it.startTimeInMs, it.endTimeInMs)
                }
            },
            onLongClickSubtitle = {
                clipboardManager.setText(AnnotatedString(it.sourceText))
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT)
                    .show()

            }
        )
    }
}

@Composable
private fun PlayerSubtitleBoard(
    subtitles: List<Subtitle>,
    playingSubtitleIndex: Int,
    activeSubtitleIndex: Int,
    activeTokenStartIndex: Int,
    loop: Pair<Long?, Long?>,
    pageSize: IntSize,
    onClickToken: (Int, Token) -> Unit,
    onClickSubtitle: (Subtitle) -> Unit,
    onDoubleClickSubtitle: (Subtitle) -> Unit,
    onLongClickSubtitle: (Subtitle) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val screenDensity = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val listState = rememberSubtitleListState(
        subtitles = subtitles,
        playingSubtitleIndex = playingSubtitleIndex,
        listSize = pageSize,
        resolver = fontFamilyResolver,
        density = screenDensity,
        direction = layoutDirection
    )

    var isScrollControlledByUser by remember { mutableStateOf(false) }
    var showIndicator by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val updatedPlayingSubtitleIndex by rememberUpdatedState(playingSubtitleIndex)
    val timer = remember {
        InteractionTimer(
            scope = scope,
            onTimerEnd = {
                isScrollControlledByUser = false
                scope.launch {
                    scrollToIndex(
                        index = updatedPlayingSubtitleIndex,
                        subtitles = subtitles,
                        listSize = pageSize,
                        state = listState,
                        resolver = fontFamilyResolver,
                        density = screenDensity,
                        direction = layoutDirection
                    )
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        SubtitleList(
            modifier = Modifier.nestedScroll(object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset, source: NestedScrollSource
                ): Offset {
                    // a workaround to detect the scroll triggered by USER
                    timer.register(TimedEvent.scrollSubtitles.value) {
                        isScrollControlledByUser = true
                        showIndicator = true
                    }
                    return super.onPreScroll(available, source)
                }
            }),
            subtitles = subtitles,
            playingSubtitleIndex = playingSubtitleIndex,
            loop = loop,
            activeSubtitleIndex = activeSubtitleIndex,
            activeTokenStartIndex = activeTokenStartIndex,
            listState = listState,
            onClickToken = { index, token ->
                scope.launch { listState.stopScroll() }
                onClickToken(index, token)
            },
            onClickSubtitle = onClickSubtitle,
            onDoubleClickSubtitle = onDoubleClickSubtitle,
            onLongClickSubtitle = onLongClickSubtitle,
        )

        if (showIndicator) {
            CenterIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (!listState.isScrollInProgress) {
        timer.unregister(
            event = TimedEvent.scrollSubtitles.value,
            onEventTimerEnd = { showIndicator = false },
        )
    }

    LaunchedEffect(activeSubtitleIndex) {
        if (activeSubtitleIndex in subtitles.indices) {
            timer.register(
                event = TimedEvent.activateSubtitle.value,
            ) {
                isScrollControlledByUser = true
            }
        } else {
            timer.unregister(
                event = TimedEvent.activateSubtitle.value,
            )
        }
    }

    LaunchedEffect(playingSubtitleIndex) {
        if (!isScrollControlledByUser) {
            scrollToIndex(
                index = playingSubtitleIndex,
                subtitles = subtitles,
                listSize = pageSize,
                state = listState,
                resolver = fontFamilyResolver,
                density = screenDensity,
                direction = layoutDirection
            )
        }
    }
}

@Composable
private fun SubtitleList(
    subtitles: List<Subtitle>,
    playingSubtitleIndex: Int,
    loop: Pair<Long?, Long?>,
    activeSubtitleIndex: Int,
    activeTokenStartIndex: Int,
    listState: LazyListState,
    onClickToken: (Int, Token) -> Unit,
    onClickSubtitle: (Subtitle) -> Unit,
    onDoubleClickSubtitle: (Subtitle) -> Unit,
    onLongClickSubtitle: (Subtitle) -> Unit,
    modifier: Modifier = Modifier,
    verticalSpace: Dp = 16.dp
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val paddingHeight = maxHeight / 2 - verticalSpace

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                // fade-in effect
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.3f to Color.Red,
                            0.7f to Color.Red,
                            1f to Color.Transparent
                        ), blendMode = BlendMode.DstIn
                    )
                },
            state = listState,
            verticalArrangement = Arrangement.spacedBy(verticalSpace),
        ) {
            item(contentType = "padding") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(paddingHeight)
                )
            }
            itemsIndexed(
                items = subtitles,
                key = { _, it -> it.hashCode() },
                contentType = { _, _ -> "content" }) { index, it ->
                SubtitleListItem(
                    sourceText = it.sourceText,
                    targetText = it.targetText,
                    tokens = it.tokens,
                    isActive = index == playingSubtitleIndex,
                    isInClip = it.isInLoop(loop),
                    activeTokenStartIndex = if (index == activeSubtitleIndex) activeTokenStartIndex
                    else -1,
                    onClickToken = { token -> onClickToken(index, token) },
                    onClickBox = { onClickSubtitle(it) },
                    onDoubleClickBox = { onDoubleClickSubtitle(it) },
                    onLongClickBox = { onLongClickSubtitle(it) },
                )
            }
            item(contentType = "padding") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(paddingHeight)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SubtitleListItem(
    modifier: Modifier = Modifier,
    sourceText: String,
    targetText: String = "",
    isActive: Boolean,
    isInClip: Boolean,
    tokens: List<Token>? = null,
    activeTokenStartIndex: Int = -1,
    onClickToken: (Token) -> Unit = {},
    onClickBox: () -> Unit = {},
    onDoubleClickBox: () -> Unit = {},
    onLongClickBox: () -> Unit = {}
) {
    SubcomposeLayout(modifier = modifier.fillMaxWidth()) { constraints ->
        // weights as modifier.weight
        val startBoxWeight = SubtitleStartBoxWeight
        val textWeight = SubtitleTextItemWeight
        val endBoxWeight = SubtitleEndBoxWeight

        // Step 1: Measure SubtitleTextItem
        val textPlaceable = subcompose("text") {
            SubtitleTextItem(
                sourceText = sourceText,
                targetText = targetText,
                tokens = tokens,
                activeTokenStartIndex = activeTokenStartIndex,
                onClickToken = onClickToken,
                isActive = isActive,
                isInClip = isInClip
            )
        }.map {
            it.measure(
                constraints.copy(
                    minWidth = (constraints.maxWidth * textWeight).toInt(),
                    maxWidth = (constraints.maxWidth * textWeight).toInt()
                )
            )
        }

        // Determine the height based on the measured SubtitleTextItem
        val textHeight = textPlaceable.maxOf { it.height }

        // Step 2: Measure the boxes with the same height as SubtitleTextItem
        val boxConstraints = constraints.copy(minHeight = textHeight, maxHeight = textHeight)
        val startBoxPlaceable = subcompose("startBox") {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .combinedClickable(
                        onClick = onClickBox,
                        onLongClick = onLongClickBox,
                        onDoubleClick = onDoubleClickBox,
                    ),
            )
        }.map {
            it.measure(
                boxConstraints.copy(
                    minWidth = (boxConstraints.maxWidth * startBoxWeight).toInt(),
                    maxWidth = (boxConstraints.maxWidth * startBoxWeight).toInt()
                )
            )
        }

        val endBoxPlaceable = subcompose("endBox") {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .combinedClickable(
                        onClick = onClickBox,
                        onLongClick = onLongClickBox,
                        onDoubleClick = onDoubleClickBox,
                    ),
            )
        }.map {
            it.measure(
                boxConstraints.copy(
                    minWidth = (boxConstraints.maxWidth * endBoxWeight).toInt(),
                    maxWidth = (boxConstraints.maxWidth * endBoxWeight).toInt()
                )
            )
        }

        // Set the layout width and height
        val width = constraints.maxWidth

        layout(width, textHeight) {
            // Place the startBox
            var xOffset = 0
            startBoxPlaceable.forEach {
                it.place(xOffset, 0)
                xOffset += it.width
            }

            // Place the SubtitleTextItem
            textPlaceable.forEach {
                it.place(xOffset, 0)
                xOffset += it.width
            }

            // Place the endBox
            endBoxPlaceable.forEach {
                it.place(xOffset, 0)
            }
        }
    }
}

@JvmInline
private value class TimedEvent(val value: String) {
    companion object {
        val scrollSubtitles = TimedEvent("scrollSubtitles")
        val activateSubtitle = TimedEvent("activateSubtitle")
    }
}


internal data class SubtitleContext(
    val index: Int,
    val subtitle: Subtitle,
    val mediaId: String,
    val language: Language
)

private fun measureActiveSubtitleOffset(
    subtitle: Subtitle,
    listSize: IntSize,
    resolver: FontFamily.Resolver,
    density: Density,
    direction: LayoutDirection
): Int {
    val sourceTextHeightPx = TextMeasurer(
        defaultFontFamilyResolver = resolver,
        defaultDensity = density,
        defaultLayoutDirection = direction
    ).measure(
        text = subtitle.sourceText,
        style = TextStyle(
            fontSize = SubtitleTextDefault.sourceTextFontSize,
            textAlign = TextAlign.Center
        ),
        constraints = Constraints(maxWidth = (listSize.width * SubtitleTextItemWeight).toInt())
    ).size.height

    return -((listSize.height - sourceTextHeightPx) shr 1)
}

@Composable
private fun rememberSubtitleListState(
    subtitles: List<Subtitle>,
    playingSubtitleIndex: Int,
    listSize: IntSize,
    resolver: FontFamily.Resolver,
    density: Density,
    direction: LayoutDirection
) = remember {
    val offset =
        if (playingSubtitleIndex in subtitles.indices) measureActiveSubtitleOffset(
            subtitle = subtitles[playingSubtitleIndex],
            listSize = listSize,
            resolver = resolver,
            density = density,
            direction = direction,
        ) else 0

    LazyListState(
        firstVisibleItemIndex = playingSubtitleIndex + 1,
        firstVisibleItemScrollOffset = offset, // include one header
    )
}

private suspend fun scrollToIndex(
    index: Int,
    subtitles: List<Subtitle>,
    listSize: IntSize,
    state: LazyListState,
    resolver: FontFamily.Resolver,
    density: Density,
    direction: LayoutDirection
) {
    var offset = 0
    if (index in subtitles.indices) {
        offset = measureActiveSubtitleOffset(
            subtitle = subtitles[index],
            listSize = listSize,
            resolver = resolver,
            density = density,
            direction = direction
        )
    }

    val actualIndex = index + 1 // add one header padding
    val isTargetOnScreen = state.layoutInfo.visibleItemsInfo.any { it.index == actualIndex }
    if (isTargetOnScreen) {
        state.animateScrollToItem(
            index = actualIndex, scrollOffset = offset
        )
    } else {
        state.scrollToItem(
            index = actualIndex, scrollOffset = offset
        )
    }
}

private fun Subtitle.isInLoop(loop: Pair<Long?, Long?>): Boolean {
    val start = loop.first
    val end = loop.second
    return if (start == null && end == null) false
    else if (end == null) endTimeInMs > start!!
    else if (start == null) startTimeInMs < end
    else !(endTimeInMs <= start || startTimeInMs >= end)
}

// combined
private const val SubtitleStartBoxWeight = 1f / 6f
private const val SubtitleTextItemWeight = 4f / 6f
private const val SubtitleEndBoxWeight = 1f / 6f

@Preview
@Composable
private fun SubtitleBoardPreview() {
    val subtitles = (0 until 50).map {
        Subtitle(
            sourceText = "source text $it",
            targetText = "target text $it",
            startTimeInMs = it * 1000L,
            endTimeInMs = it * 1000L + 1000L,
            tokens = listOf(
                Token(0, 6, "source"),
                Token(7, 11, "text"),
                Token(12, 12 + it.toString().length, it.toString())
            )
        )
    }

    var activeTokenStartIndex by remember { mutableIntStateOf(-1) }

    var playingSubtitleIndex by remember { mutableIntStateOf(10) }

    var activeSubtitleIndex by remember { mutableIntStateOf(-1) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val size = with(LocalDensity.current) {
            IntSize(
                width = maxWidth.roundToPx(),
                height = maxHeight.roundToPx()
            )
        }
        PlayerSubtitleBoard(
            subtitles = subtitles,
            playingSubtitleIndex = playingSubtitleIndex,
            activeSubtitleIndex = activeSubtitleIndex,
            activeTokenStartIndex = activeTokenStartIndex,
            loop = Pair(null, null),
            pageSize = size,
            onClickToken = { index, token ->
                activeSubtitleIndex = index
                activeTokenStartIndex = token.startIndex
            },
            onClickSubtitle = {
                playingSubtitleIndex = subtitles.indexOf(it)
            },
            onDoubleClickSubtitle = {
                activeSubtitleIndex = -1
                activeTokenStartIndex = -1
            },
            onLongClickSubtitle = {}
        )
    }
}