package com.coda.situlearner.core.model.feature.mapper

import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.feature.UserRating
import kotlinx.datetime.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun MeaningQuizStats.updateWith(rating: UserRating): MeaningQuizStats {
    val (newInterval, newEaseFactor) = (intervalDays to easeFactor).updateWith(rating)

    return copy(
        easeFactor = newEaseFactor,
        intervalDays = newInterval,
        nextQuizDate = calcNextQuizDate(newInterval)
    )
}

fun TranslationQuizStats.updateWith(rating: UserRating): TranslationQuizStats {
    val (newInterval, newEaseFactor) = (intervalDays to easeFactor).updateWith(rating)

    return copy(
        easeFactor = newEaseFactor,
        intervalDays = newInterval,
        nextQuizDate = calcNextQuizDate(newInterval)
    )
}

fun MeaningQuizStats.toWordProficiency() = calcProficiency(intervalDays)

fun TranslationQuizStats.toWordProficiency() = calcProficiency(intervalDays)

private fun calcProficiency(intervalDays: Int) = when {
    intervalDays == 0 -> WordProficiency.Unset
    intervalDays <= 2 -> WordProficiency.Beginner
    intervalDays <= 7 -> WordProficiency.Intermediate
    else -> WordProficiency.Proficient
}

private fun calcNextQuizDate(interval: Int) = Clock.System.now()
    .plus(interval.toLong().toDuration(DurationUnit.DAYS))

private fun Pair<Int, Double>.updateWith(rating: UserRating): Pair<Int, Double> {
    // refer to sm-2
    val oldInterval = this.first
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