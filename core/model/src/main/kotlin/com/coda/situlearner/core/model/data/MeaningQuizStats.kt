package com.coda.situlearner.core.model.data

import kotlinx.datetime.Instant

data class MeaningQuizStats(
    val wordId: String,
    val easeFactor: Double,
    val intervalDays: Int,
    val nextQuizDate: Instant
)