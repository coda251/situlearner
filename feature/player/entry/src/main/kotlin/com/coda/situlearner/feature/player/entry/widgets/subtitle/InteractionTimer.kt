package com.coda.situlearner.feature.player.entry.widgets.subtitle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InteractionTimer(
    private val scope: CoroutineScope,
    private val duration: Long = 3000L,
    private val onTimerEnd: () -> Unit = {}
) {
    private val eventTimestamp = mutableMapOf<String, Long>()

    fun register(
        event: String,
        onEventStart: () -> Unit = {}
    ) {
        eventTimestamp[event] = System.currentTimeMillis()
        onEventStart()
    }

    fun unregister(
        event: String,
        onEventTimerEnd: (event: String) -> Unit = {}
    ) {
        eventTimestamp[event]?.let { timestamp ->
            scope.launch {
                delay(duration)
                if (eventTimestamp[event] == timestamp) {
                    eventTimestamp.remove(event)
                    onEventTimerEnd(event)

                    if (eventTimestamp.isEmpty()) onTimerEnd()
                }
            }
        }
    }
}