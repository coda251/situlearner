package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import com.coda.situlearner.core.model.infra.Token

@Composable
fun SubtitleListItem(
    modifier: Modifier = Modifier,
    sourceText: String,
    targetText: String = "",
    isActive: Boolean,
    isInClip: Boolean,
    showTargetText: Boolean = true,
    tokens: List<Token>? = null,
    activeTokenStartIndex: Int = -1,
    onClickToken: (Token) -> Unit = {},
    onClickStartBox: () -> Unit = {},
    onClickEndBox: () -> Unit = {},
    onDoubleClickBox: () -> Unit = {},
    onLongClickBox: () -> Unit = {},
    subtitleTextColors: SubtitleTextColors = SubtitleTextDefault.subtitleTextColors()
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
                isInClip = isInClip,
                showTargetText = showTargetText,
                subtitleColors = subtitleTextColors
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
                        onClick = onClickStartBox,
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
                        onClick = onClickEndBox,
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

// combined
const val SubtitleStartBoxWeight = 1f / 6f
const val SubtitleTextItemWeight = 4f / 6f
const val SubtitleEndBoxWeight = 1f / 6f