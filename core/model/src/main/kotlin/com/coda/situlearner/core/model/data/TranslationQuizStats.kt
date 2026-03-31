package com.coda.situlearner.core.model.data

import kotlin.time.Instant

@ConsistentCopyVisibility
data class TranslationQuizStats internal constructor(
    val wordId: String,
    val easeFactor: Double,
    val intervalDays: Int,
    val nextQuizDate: Instant,
    val lastQuestion: String,
    val userAnswer: String
) {
    companion object {
        fun create(
            wordId: String,
            currentDate: Instant,
            lastQuestion: String = "",
            userAnswer: String = "",
        ) = TranslationQuizStats(
            wordId = wordId,
            easeFactor = 2.5,
            intervalDays = 0,
            nextQuizDate = currentDate,
            lastQuestion = lastQuestion,
            userAnswer = userAnswer,
        )

        fun fromDb(
            wordId: String,
            easeFactor: Double,
            intervalDays: Int,
            nextQuizDate: Instant,
            lastQuestion: String,
            userAnswer: String
        ) = TranslationQuizStats(
            wordId = wordId,
            easeFactor = easeFactor,
            intervalDays = intervalDays,
            nextQuizDate = nextQuizDate,
            lastQuestion = lastQuestion,
            userAnswer = userAnswer
        )
    }

    fun updateQuestionAndAnswer(question: String, answer: String) = copy(
        lastQuestion = question,
        userAnswer = answer
    )
}