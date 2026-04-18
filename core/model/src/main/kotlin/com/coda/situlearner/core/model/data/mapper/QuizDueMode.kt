package com.coda.situlearner.core.model.data.mapper

import com.coda.situlearner.core.model.data.QuizDueMode
import kotlinx.datetime.DateTimeUnit
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

fun QuizDueMode.toInstant(
    now: Instant,
    timeZone: TimeZone
) = when(this) {
    QuizDueMode.Now -> now
    QuizDueMode.Today -> {
        val today = now.toLocalDateTime(timeZone).date
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        tomorrow.atStartOfDayIn(timeZone)
    }
}