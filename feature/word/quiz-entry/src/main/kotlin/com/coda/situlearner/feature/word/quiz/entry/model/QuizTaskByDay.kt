package com.coda.situlearner.feature.word.quiz.entry.model

import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.feature.UserRating
import com.coda.situlearner.core.model.feature.mapper.calcNextQuizDate
import com.coda.situlearner.core.model.feature.mapper.calcProficiency
import com.coda.situlearner.core.model.feature.mapper.toWordProficiency
import com.coda.situlearner.core.model.feature.mapper.updateWith
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

internal data class QuizTaskByDay(
    val numMeaningUnset: Int,
    val numMeaningBeginner: Int,
    val numMeaningIntermediate: Int,
    val numMeaningProficient: Int,
    val numTranslationUnset: Int,
    val numTranslationBeginner: Int,
    val numTranslationIntermediate: Int,
    val numTranslationProficient: Int,
    val numTranslationPredicted: Int,
    val date: LocalDate,
    val dayIndex: Int,
) {
    val numMeaning: Int =
        numMeaningUnset + numMeaningBeginner + numMeaningIntermediate + numMeaningProficient

    val numTranslation: Int =
        numTranslationUnset + numTranslationBeginner + numTranslationIntermediate +
                numTranslationProficient + numTranslationPredicted
}

internal fun Pair<List<MeaningQuizStats>, List<TranslationQuizStats>>.asTasks(
    currentDate: Instant = Clock.System.now(),
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    dayWindow: Int = 14,
): List<QuizTaskByDay> {

    val (meaningStatsList, translationStatsList) = this
    val maxDayIndex = dayWindow - 1
    val today = currentDate.toLocalDateTime(timeZone).date

    val groupedMeaningStats = meaningStatsList
        .mapNotNull { stats ->
            mapDateToDayIndex(
                today = today,
                other = stats.nextQuizDate.toLocalDateTime(timeZone).date,
                maxDayIndex = maxDayIndex
            )?.let { it to stats.toWordProficiency() }
        }
        .groupBy(keySelector = { it.first }, valueTransform = { it.second })

    val groupedTranslationStats = translationStatsList
        .mapNotNull { stats ->
            mapDateToDayIndex(
                today = today,
                other = stats.nextQuizDate.toLocalDateTime(timeZone).date,
                maxDayIndex = maxDayIndex
            )?.let { it to stats.toWordProficiency() }
        }
        .groupBy(keySelector = { it.first }, valueTransform = { it.second })

    val predictedCounts = IntArray(dayWindow)
    meaningStatsList.forEach { stats ->
        // NOTE: only words with intermediate proficiency are predicted
        if (stats.toWordProficiency() == WordProficiency.Intermediate) {
            predictTranslationTriggerDate(stats, currentDate)?.let {
                mapDateToDayIndex(
                    today = today,
                    other = it.toLocalDateTime(timeZone).date,
                    maxDayIndex = maxDayIndex
                )
            }?.let {
                predictedCounts[it]++
            }
        }
    }

    return List(dayWindow) { i ->
        val mList = groupedMeaningStats[i] ?: emptyList()
        val tList = groupedTranslationStats[i] ?: emptyList()

        QuizTaskByDay(
            date = today.plus(i, DateTimeUnit.DAY),
            dayIndex = i,
            numMeaningUnset = mList.count { it == WordProficiency.Unset },
            numMeaningBeginner = mList.count { it == WordProficiency.Beginner },
            numMeaningIntermediate = mList.count { it == WordProficiency.Intermediate },
            numMeaningProficient = mList.count { it == WordProficiency.Proficient },
            numTranslationUnset = tList.count { it == WordProficiency.Unset },
            numTranslationBeginner = tList.count { it == WordProficiency.Beginner },
            numTranslationIntermediate = tList.count { it == WordProficiency.Intermediate },
            numTranslationProficient = tList.count { it == WordProficiency.Proficient },
            numTranslationPredicted = predictedCounts[i]
        )
    }
}

private fun mapDateToDayIndex(
    today: LocalDate,
    other: LocalDate,
    maxDayIndex: Int,
): Int? {
    val daysBetween = today.daysUntil(other)
    val dayIndex = daysBetween.coerceAtLeast(0)
    // two weeks time window
    return if (dayIndex <= maxDayIndex) dayIndex
    else null
}

private fun predictTranslationTriggerDate(
    stats: MeaningQuizStats,
    now: Instant,
): Instant? {
    var currentInterval = stats.intervalDays
    var currentEF = stats.easeFactor
    // overdue words will be quizzed now
    var currentQuizDate = stats.nextQuizDate.coerceAtLeast(now)

    if (calcProficiency(currentInterval) == WordProficiency.Proficient) {
        return currentQuizDate
    }

    repeat(10) {
        // simulate: if user rating is good at currentQuizDate
        val (nextInterval, nextEF) = (currentInterval to currentEF).updateWith(UserRating.Good)
        currentInterval = nextInterval
        currentEF = nextEF
        if (calcProficiency(currentInterval) == WordProficiency.Proficient) {
            return currentQuizDate
        }

        // update currentQuizDate
        currentQuizDate = calcNextQuizDate(currentInterval, currentQuizDate)
    }

    return null
}