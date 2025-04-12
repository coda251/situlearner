package com.coda.situlearner.feature.player.entry.widgets.subtitle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.core.model.infra.Token

@Composable
internal fun SubtitleTextItem(
    modifier: Modifier = Modifier,
    sourceText: String,
    targetText: String = "",
    tokens: List<Token>? = null,
    activeTokenStartIndex: Int = -1,
    onClickToken: (Token) -> Unit = {},
    isActive: Boolean = false,
    isInClip: Boolean = false,
    subtitleColors: SubtitleTextColors = SubtitleTextDefault.subtitleTextColors()
) {
    val sourceTextColor by subtitleColors.sourceTextColor(isActive, isInClip)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextWithClickableTokens(
                text = sourceText,
                tokens = tokens,
                activeTokenStartIndex = activeTokenStartIndex,
                onClickToken = onClickToken,
                textStyle = TextStyle(
                    color = sourceTextColor,
                    fontSize = SubtitleTextDefault.sourceTextFontSize,
                    textAlign = TextAlign.Center
                ),
                activeTokenStyle = SpanStyle(
                    color = subtitleColors.activeTokenTextColor,
                    background = subtitleColors.activeTokenBackgroundColor,
                )
            )
            Text(
                text = targetText,
                fontSize = SubtitleTextDefault.targetTextFontSize,
                style = TextStyle(
                    color = subtitleColors.defaultTargetTextColor
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
@Suppress("DEPRECATION")
private fun TextWithClickableTokens(
    text: String,
    tokens: List<Token>?,
    activeTokenStartIndex: Int = -1,
    onClickToken: (Token) -> Unit = {},
    textStyle: TextStyle = TextStyle.Default,
    activeTokenStyle: SpanStyle = TextStyle.Default.toSpanStyle(),
) {

    val annotatedString = tokens?.let {
        buildAnnotatedString {
            var lastEnd = 0
            tokens.forEach {
                val start = it.startIndex
                val end = it.endIndex

                // add left spacer (if exists)
                if (start > lastEnd) append(text.substring(lastEnd, start))

                // add token
                val token = text.substring(start, end)
                pushStringAnnotation(it.lemma, token) // attach lemma data in tag

                // add style for active token (be clicked)
                if (start == activeTokenStartIndex) withStyle(style = activeTokenStyle) {
                    append(token)
                }
                else append(token)
                pop()

                lastEnd = end
            }
            // add the end section
            if (lastEnd < text.length) append(text.substring(lastEnd, text.length))
        }
    } ?: AnnotatedString(text = text)

    ClickableText(
        text = annotatedString,
        style = textStyle,
        onClick = { offset ->
            annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let {
                onClickToken(Token(startIndex = it.start, endIndex = it.end, lemma = it.tag))
            }
        }
    )
}

/**
 * @param tokens The tokens.
 * The input should be sorted by the first index and have no intersection between pairs.
 * @param activeTokenStartIndex First value of token index pair.
 * @param onClickToken Token item (String), index of the first character of token item (Int).
 */
@Composable
// FIXME: seems like an internal bug in LinkAnnotation. If the token is located at the end of a
//  line and the line is not the last line, then the click area for this token could be
//  incorrectly positioned at the start of the line. See SubtitleListItemPreview.
//  As a workaround, we currently use TextWithClickableTokens with deprecated ClickableText and
//  wait for the upgrade of compose library.
@Suppress("UNUSED")
private fun TextWithClickableTokensToBeFixed(
    text: String,
    tokens: List<Token>?,
    activeTokenStartIndex: Int = -1,
    onClickToken: (Token) -> Unit = {},
    textStyle: TextStyle = TextStyle.Default,
    activeTokenStyle: SpanStyle = TextStyle.Default.toSpanStyle()
) {
    val annotatedString = remember(text, tokens, activeTokenStartIndex) {
        tokens?.let { tokenList ->
            buildTokenizedText(
                text = text,
                tokens = tokenList,
                activeTokenStartIndex = activeTokenStartIndex,
                activeTokenStyle = activeTokenStyle,
                onClickToken = onClickToken
            )
        } ?: AnnotatedString(text = text)
    }

    BasicText(text = annotatedString, style = textStyle)
}

private fun buildTokenizedText(
    text: String,
    tokens: List<Token>,
    activeTokenStartIndex: Int = -1,
    activeTokenStyle: SpanStyle = TextStyle.Default.toSpanStyle(),
    onClickToken: (Token) -> Unit
) = buildAnnotatedString {
    var lastEnd = 0
    tokens.forEach { token ->
        val start = token.startIndex
        val end = token.endIndex

        // add left spacer (if exists)
        if (start > lastEnd) append(text.substring(lastEnd, start))

        // add token
        val tokenString = text.substring(start, end)
        pushLink(LinkAnnotation.Clickable(token.lemma, linkInteractionListener = {
            onClickToken(
                Token(
                    startIndex = token.startIndex,
                    endIndex = token.endIndex,
                    lemma = token.lemma
                )
            )
        }))

        // add style for active token (be clicked)
        if (start == activeTokenStartIndex) withStyle(style = activeTokenStyle) {
            append(tokenString)
        }
        else append(tokenString)
        pop()

        lastEnd = end
    }
    // add the end section
    if (lastEnd < text.length) append(text.substring(lastEnd, text.length))
}

object SubtitleTextDefault {

    val sourceTextFontSize = 22.sp
    val targetTextFontSize = 12.sp

    @Composable
    fun subtitleTextColors(
        defaultSourceTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        activeSourceTextColor: Color = MaterialTheme.colorScheme.primary,
        inClipSourceTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        defaultTargetTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        activeTokenTextColor: Color = MaterialTheme.colorScheme.primary,
        activeTokenBackgroundColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
    ): SubtitleTextColors = SubtitleTextColors(
        defaultSourceTextColor = defaultSourceTextColor,
        activeSourceTextColor = activeSourceTextColor,
        inClipSourceTextColor = inClipSourceTextColor,
        defaultTargetTextColor = defaultTargetTextColor,
        activeTokenTextColor = activeTokenTextColor,
        activeTokenBackgroundColor = activeTokenBackgroundColor,
    )
}

@Immutable
class SubtitleTextColors internal constructor(
    private val defaultSourceTextColor: Color,
    private val activeSourceTextColor: Color,
    private val inClipSourceTextColor: Color,
    val defaultTargetTextColor: Color,
    val activeTokenTextColor: Color,
    val activeTokenBackgroundColor: Color
) {
    @Composable
    fun sourceTextColor(
        isActive: Boolean,
        isInClip: Boolean
    ): State<Color> {
        return rememberUpdatedState(
            if (isActive) activeSourceTextColor
            else {
                if (isInClip) inClipSourceTextColor
                else defaultSourceTextColor
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SubtitleListItemPreview() {

    val subtitle = Subtitle(
        sourceText = "孤独と我慢の日々は終わった",
        targetText = "孤独与忍耐的时光终于结束",
        tokens = listOf(
            Token(0, 2, "孤独"),
            Token(2, 3, "と"),
            Token(3, 5, "我慢"),
            Token(5, 6, "の"),
            Token(6, 8, "日々"),
            Token(8, 9, "は"),
            Token(9, 12, "終わる"),
            Token(12, 13, "た")
        ),
        startTimeInMs = 0L,
        endTimeInMs = 1000L,
    )

    var activeTokenStartIndex by remember {
        mutableIntStateOf(-1)
    }

    Row {
        Box(modifier = Modifier.weight(1f))
        SubtitleTextItem(
            sourceText = subtitle.sourceText,
            targetText = subtitle.targetText,
            isActive = true,
            isInClip = false,
            tokens = subtitle.tokens,
            activeTokenStartIndex = activeTokenStartIndex,
            onClickToken = {
                activeTokenStartIndex = it.startIndex
            },
            modifier = Modifier.weight(4f)
        )
        Box(modifier = Modifier.weight(1f))
    }
}