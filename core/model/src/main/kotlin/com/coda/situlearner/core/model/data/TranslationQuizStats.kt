package com.coda.situlearner.core.model.data

import kotlinx.datetime.Instant

data class TranslationQuizStats(
    val wordId: String,
    val easeFactor: Double,
    val intervalDays: Int,
    val nextQuizDate: Instant,
    val lastQuestion: String,
    val userAnswer: String
)