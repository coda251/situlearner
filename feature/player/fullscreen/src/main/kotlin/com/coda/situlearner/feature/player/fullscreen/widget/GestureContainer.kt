package com.coda.situlearner.feature.player.fullscreen.widget

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun GestureContainer(
    onTap: () -> Unit,
    onDoubleTapLeft: () -> Unit,
    onDoubleTapRight: () -> Unit,
    onDoubleTapCenter: () -> Unit,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val prominentRipple = ripple(
        bounded = true,
        color = Color.White.copy(alpha = 0.5f)
    )

    val leftInteractionSource = remember { MutableInteractionSource() }
    val centerInteractionSource = remember { MutableInteractionSource() }
    val rightInteractionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    fun triggerRipple(source: MutableInteractionSource, pos: Offset) {
        scope.launch {
            val press = PressInteraction.Press(pos)
            source.emit(press)
            delay(80)
            source.emit(PressInteraction.Release(press))
        }
    }

    Row(modifier = modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .indication(leftInteractionSource, prominentRipple)
                .gesture(
                    onTap = onTap,
                    onDoubleTap = {
                        onDoubleTapLeft()
                        triggerRipple(leftInteractionSource, Offset(0f, it.height / 2f))
                    },
                    onLongPressStart = onLongPressStart,
                    onLongPressEnd = onLongPressEnd
                )
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f)
                .indication(centerInteractionSource, prominentRipple)
                .gesture(
                    onTap = onTap,
                    onDoubleTap = {
                        onDoubleTapCenter()
                        triggerRipple(
                            centerInteractionSource,
                            Offset(it.width / 2f, it.height / 2f)
                        )
                    },
                    onLongPressStart = onLongPressStart,
                    onLongPressEnd = onLongPressEnd
                )
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .indication(rightInteractionSource, prominentRipple)
                .gesture(
                    onTap = onTap,
                    onDoubleTap = { size ->
                        onDoubleTapRight()
                        triggerRipple(
                            rightInteractionSource,
                            Offset(size.width.toFloat(), size.height / 2f)
                        )
                    },
                    onLongPressStart = onLongPressStart,
                    onLongPressEnd = onLongPressEnd
                )
        )
    }
}

private fun Modifier.gesture(
    onTap: () -> Unit,
    onDoubleTap: (size: IntSize) -> Unit,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit,
) = this
    .pointerInput(Unit) {
        detectTapGestures(
            onTap = { onTap() },
            onDoubleTap = { _ -> onDoubleTap(size) },
            onLongPress = { onLongPressStart() },
            onPress = {
                try {
                    awaitRelease()
                } finally {
                    onLongPressEnd()
                }
            }
        )
    }
    .pointerInput(Unit) {
        // intercept drag gesture to avoid being captured by tap gesture
        detectDragGestures(
            onDrag = { _, _ -> }
        )
    }