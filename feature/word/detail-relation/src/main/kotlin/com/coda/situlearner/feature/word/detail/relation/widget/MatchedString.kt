package com.coda.situlearner.feature.word.detail.relation.widget

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.coda.situlearner.feature.word.detail.relation.model.WordMatchResult

internal const val HIGHLIGHT_LEMMA_SIMILARITY = 0.6
internal const val HIGHLIGHT_PRONUNCIATION_SIMILARITY = 0.7

@Composable
internal fun WordMatchResult.lemmaAnnotated(): AnnotatedString {
    val color =
        if (lemmaSimilarity >= HIGHLIGHT_LEMMA_SIMILARITY) MaterialTheme.colorScheme.primary
        else LocalContentColor.current
    val fontWeight =
        if (lemmaSimilarity >= HIGHLIGHT_LEMMA_SIMILARITY) FontWeight.SemiBold else
            null
    return buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = color,
                fontWeight = fontWeight
            )
        ) {
            append(lemma)
        }
    }
}

@Composable
internal fun WordMatchResult.pronunciationAnnotated(): AnnotatedString {
    val color =
        if (pronunciationSimilarity >= HIGHLIGHT_PRONUNCIATION_SIMILARITY) MaterialTheme.colorScheme.primary
        else LocalContentColor.current
    val fontWeight =
        if (pronunciationSimilarity >= HIGHLIGHT_PRONUNCIATION_SIMILARITY) FontWeight.SemiBold else
            null
    if (pronunciation == null || matchedPronunciationStartIndex == -1) return AnnotatedString(
        ""
    )
    else return buildAnnotatedString {
        append(pronunciation.take(matchedPronunciationStartIndex))
        withStyle(
            style = SpanStyle(
                color = color,
                fontWeight = fontWeight
            )
        ) {
            append(
                pronunciation.substring(
                    matchedPronunciationStartIndex,
                    matchedPronunciationEndIndex
                )
            )
        }
        append(
            pronunciation.substring(
                matchedPronunciationEndIndex,
                pronunciation.length
            )
        )
    }
}