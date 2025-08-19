package com.coda.situlearner.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.coda.situlearner.core.ui.R
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

const val UndefinedTimeText = "--:--"

fun Instant.asTimeText(): String {
    val timeZone = TimeZone.currentSystemDefault()
    val inputDate = toLocalDateTime(timeZone)

    val dayString = "${inputDate.year}-${inputDate.month.number}-${inputDate.day}"
    val minuteString = "${inputDate.hour}:${inputDate.minute.toString().padStart(2, '0')}"
    return "$dayString $minuteString"
}

fun Long.asTimeText() = milliseconds.toComponents { hours, minutes, seconds, _ ->
    if (hours > 0) "%02d:%02d:%02d".format(hours, minutes, seconds)
    else "%02d:%02d".format(minutes, seconds)
}

@Composable
internal fun formatInstant(
    input: Instant,
    currentTime: Instant = Clock.System.now(),
): String {
    val timeZone = TimeZone.currentSystemDefault()
    val currentDateTime = currentTime.toLocalDateTime(timeZone)
    val inputDateTime = input.toLocalDateTime(timeZone)

    val currentDate = currentDateTime.date
    val inputDate = inputDateTime.date

    return when {
        inputDate == currentDate -> {
            "%02d:%02d".format(inputDateTime.hour, inputDateTime.minute)
        }

        inputDate == currentDate.minus(DatePeriod(days = 1)) -> {
            stringResource(R.string.core_ui_yesterday)
        }

        inputDate.isInSameWeekAs(currentDate) -> {
            when (inputDate.dayOfWeek) {
                DayOfWeek.MONDAY -> stringResource(R.string.core_ui_monday)
                DayOfWeek.TUESDAY -> stringResource(R.string.core_ui_tuesday)
                DayOfWeek.WEDNESDAY -> stringResource(R.string.core_ui_wednesday)
                DayOfWeek.THURSDAY -> stringResource(R.string.core_ui_thursday)
                DayOfWeek.FRIDAY -> stringResource(R.string.core_ui_friday)
                DayOfWeek.SATURDAY -> stringResource(R.string.core_ui_saturday)
                DayOfWeek.SUNDAY -> stringResource(R.string.core_ui_sunday)
            }
        }

        inputDate.year == currentDate.year -> {
            "${inputDate.month.number}-${inputDate.day}"
        }

        else -> {
            "${inputDate.year}-${inputDate.month.number}-${inputDate.day}"
        }
    }
}

private fun LocalDate.isInSameWeekAs(other: LocalDate): Boolean {
    val dayOfWeek = this.dayOfWeek.ordinal // 0 (Monday) to 6 (Sunday)
    val startOfWeek = this.plus(DatePeriod(days = -dayOfWeek))
    val endOfWeek = startOfWeek.plus(DatePeriod(days = 6))
    return other in startOfWeek..endOfWeek
}