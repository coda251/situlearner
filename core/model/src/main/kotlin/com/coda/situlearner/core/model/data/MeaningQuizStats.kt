package com.coda.situlearner.core.model.data

import kotlin.time.Instant

@ConsistentCopyVisibility
data class MeaningQuizStats internal constructor(
    val wordId: String,
    val easeFactor: Double,
    val intervalDays: Int,
    val nextQuizDate: Instant
) {
    companion object {
        fun create(wordId: String, currentDate: Instant) = MeaningQuizStats(
            wordId = wordId,
            easeFactor = 2.5,
            intervalDays = 0,
            nextQuizDate = currentDate
        )

        fun fromDb(
            wordId: String,
            easeFactor: Double,
            intervalDays: Int,
            nextQuizDate: Instant
        ) = MeaningQuizStats(
            wordId = wordId,
            easeFactor = easeFactor,
            intervalDays = intervalDays,
            nextQuizDate = nextQuizDate
        )
    }
}