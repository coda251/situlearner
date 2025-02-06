package com.coda.situlearner.feature.player.entry.widgets.seekbar

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToLong

/**
 * Referred to https://github.com/vfsfitvnm/ViMusic, with some modification.
 */
@Composable
internal fun SeekBar(
    start: Long,
    end: Long,
    valueProvider: () -> Long,
    onDragStart: (Long) -> Unit,
    onDrag: (Long) -> Unit,
    onDragEnd: () -> Unit,
    colors: SeekBarColors,
    modifier: Modifier = Modifier,
    thumbRadius: Dp = 6.dp,
    trackHeight: Dp = 3.dp
) {
    check(start <= end)

    val isDragging = remember(start, end) { MutableTransitionState(false) }
    val transition = updateTransition(targetState = isDragging.targetState, label = "")

    val animatedTrackHeight by transition.animateDp(label = "") {
        if (it) thumbRadius else trackHeight  // set to 6.dp
    }
    val animatedThumbRadius by transition.animateDp(label = "") {
        if (it) 0.dp else thumbRadius
    }

    Box(modifier = modifier
        .padding(
            vertical = 12.dp,
            horizontal = 12.dp
        )
        .pointerInput(start, end) {
            detectTapGestures(
                onPress = { offset ->
                    onDragStart((offset.x / size.width * (end - start) + start).roundToLong())
                },
                onTap = {
                    onDragEnd()
                }
            )
        }
        .pointerInput(start, end) {
            var acc = 0f

            detectHorizontalDragGestures(
                onDragStart = {
                    isDragging.targetState = true
                },
                onHorizontalDrag = { _, delta ->
                    acc += delta / size.width * (end - start)

                    if (acc !in -1f..1f) {
                        onDrag(acc.toLong())
                        acc -= acc.toLong()
                    }
                },
                onDragEnd = {
                    isDragging.targetState = false
                    acc = 0f
                    onDragEnd()
                },
                onDragCancel = {
                    isDragging.targetState = false
                    acc = 0f
                    onDragEnd()
                }
            )
        }
        .drawBehind {
            val thumbPosition: Float
            val value: Long = valueProvider()

            thumbPosition =
                calcFraction(start.toFloat(), end.toFloat(), value.toFloat()) * size.width

            drawLine(
                color = colors.inactiveTrackColor,
                start = center.copy(x = 0f),
                end = center.copy(x = size.width),
                strokeWidth = animatedTrackHeight.toPx(),
                cap = StrokeCap.Round
            )

            drawLine(
                color = colors.activeTrackColor,
                start = center.copy(x = 0f),
                end = center.copy(x = thumbPosition),
                strokeWidth = animatedTrackHeight.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(
                color = colors.thumbColor,
                radius = animatedThumbRadius.toPx(),
                center = center.copy(x = thumbPosition)
            )
        }
    )
}

internal class SeekBarColors(
    val thumbColor: Color,
    val activeTrackColor: Color,
    val inactiveTrackColor: Color,
)

private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)