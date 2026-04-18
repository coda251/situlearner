package com.coda.situlearner.core.model.feature.mapper

import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.feature.UserRating
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

fun MeaningQuizStats.updateWith(
    rating: UserRating,
    currentQuizDate: Instant
): MeaningQuizStats {
    val (newInterval, newEaseFactor) = (intervalDays to easeFactor).updateWith(rating)

    return copy(
        easeFactor = newEaseFactor,
        intervalDays = newInterval,
        nextQuizDate = calcNextQuizDate(newInterval, currentQuizDate)
    )
}

fun TranslationQuizStats.updateWith(
    rating: UserRating,
    currentQuizDate: Instant
): TranslationQuizStats {
    val (newInterval, newEaseFactor) = (intervalDays to easeFactor).updateWith(rating)

    return copy(
        easeFactor = newEaseFactor,
        intervalDays = newInterval,
        nextQuizDate = calcNextQuizDate(newInterval, currentQuizDate)
    )
}

fun MeaningQuizStats.toWordProficiency() = calcProficiency(intervalDays)

fun TranslationQuizStats.toWordProficiency() = calcProficiency(intervalDays)

fun calcProficiency(intervalDays: Int) = when {
    intervalDays == 0 -> WordProficiency.Unset
    intervalDays <= 2 -> WordProficiency.Beginner
    intervalDays <= 7 -> WordProficiency.Intermediate
    else -> WordProficiency.Proficient
}

fun calcNextQuizDate(
    interval: Int,
    currentQuizDate: Instant
    // we currently did not take overdue days into the calculation
) = currentQuizDate + interval.toLong().days

fun Pair<Int, Double>.updateWith(rating: UserRating): Pair<Int, Double> {
    // refer to sm-2
    // to simplify, new words (interval = 0) will be set to 1
    val oldInterval = this.first.coerceAtLeast(1)
    val oldEaseFactor = this.second

    val newEaseFactor: Double
    val newInterval: Int

    when (rating) {
        UserRating.Again -> {
            newInterval = 1
            newEaseFactor = (oldEaseFactor - 0.2).coerceAtLeast(1.3)
        }

        UserRating.Hard -> {
            newInterval = (oldInterval * 1.2).toInt().coerceAtLeast(1)
            newEaseFactor = (oldEaseFactor - 0.15).coerceAtLeast(1.3)
        }

        UserRating.Good -> {
            newInterval = (oldInterval * oldEaseFactor).toInt().coerceAtLeast(1)
            newEaseFactor = oldEaseFactor
        }

        UserRating.Easy -> {
            newInterval = (oldInterval * oldEaseFactor * 1.3).toInt().coerceAtLeast(1)
            newEaseFactor = oldEaseFactor + 0.15
        }
    }

    return newInterval to newEaseFactor
}