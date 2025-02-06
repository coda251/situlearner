package com.coda.situlearner.feature.word.category.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.coda.situlearner.feature.word.category.R
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.DayOfWeek
import java.util.Locale

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
            stringResource(R.string.word_category_screen_yesterday)
        }

        inputDate.isInSameWeekAs(currentDate) -> {
            when (inputDate.dayOfWeek) {
                DayOfWeek.MONDAY -> stringResource(R.string.word_category_screen_monday)
                DayOfWeek.TUESDAY -> stringResource(R.string.word_category_screen_tuesday)
                DayOfWeek.WEDNESDAY -> stringResource(R.string.word_category_screen_wednesday)
                DayOfWeek.THURSDAY -> stringResource(R.string.word_category_screen_thursday)
                DayOfWeek.FRIDAY -> stringResource(R.string.word_category_screen_friday)
                DayOfWeek.SATURDAY -> stringResource(R.string.word_category_screen_saturday)
                DayOfWeek.SUNDAY -> stringResource(R.string.word_category_screen_sunday)
            }
        }

        inputDate.year == currentDate.year -> {
            "${
                inputDate.month.name.lowercase()
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
            } ${inputDate.dayOfMonth}"
        }

        else -> {
            "${inputDate.year}-${inputDate.monthNumber}-${inputDate.dayOfMonth}"
        }
    }
}

internal fun LocalDate.isInSameWeekAs(other: LocalDate): Boolean {
    val dayOfWeek = this.dayOfWeek.ordinal // 0 (Monday) to 6 (Sunday)
    val startOfWeek = this.plus(DatePeriod(days = -dayOfWeek))
    val endOfWeek = startOfWeek.plus(DatePeriod(days = 6))
    return other in startOfWeek..endOfWeek
}