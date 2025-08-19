package com.coda.situlearner.core.model.data

import kotlin.time.Instant

data class MeaningQuizStats(
    val wordId: String,
    val easeFactor: Double,
    val intervalDays: Int,
    val nextQuizDate: Instant
)