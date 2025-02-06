package com.coda.situlearner.core.model.domain

import com.coda.situlearner.core.model.domain.TimeFrame.Never
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeArithmeticException
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil

enum class TimeFrame(val range: ClosedRange<Int>, val level: Int) {
    Never(Int.MIN_VALUE..-1, 0),
    Today(0..0, 1),
    LastThreeDays(1..3, 2),
    LastWeek(4..7, 3),
    LastTwoWeeks(8..14, 4),
    LastMonth(15..30, 5),
    OverAMonth(31..Int.MAX_VALUE, 6);

    companion object {
        val defaultTimeFrameProvider: (Instant?) -> TimeFrame = {
            it?.toTimeFrame() ?: Never
        }
    }
}

fun Instant.toTimeFrame(
    current: Instant = Clock.System.now(),
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): TimeFrame {
    val dayInterval = if (current < this) -1
    else {
        try {
            daysUntil(current, timeZone)
        } catch (e: DateTimeArithmeticException) {
            -1
        }
    }
    return TimeFrame.entries.find { dayInterval in it.range } ?: Never
}