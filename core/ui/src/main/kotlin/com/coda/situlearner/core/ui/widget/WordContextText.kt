package com.coda.situlearner.core.ui.widget

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.coda.situlearner.core.model.data.WordContext

@Composable
fun WordContextText(
    wordContext: WordContext,
    spanStyle: SpanStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
    ),
) {
    val subtitleSourceText = wordContext.subtitleSourceText
    val startIndex = wordContext.wordStartIndex
    val endIndex = wordContext.wordEndIndex

    val annotatedText = AnnotatedString.Builder().apply {
        append(subtitleSourceText.substring(0, startIndex))
        withStyle(style = spanStyle) {
            append(subtitleSourceText.substring(startIndex, endIndex))
        }
        append((subtitleSourceText.substring(endIndex, subtitleSourceText.length)))
    }.toAnnotatedString()

    Text(text = annotatedText)
}