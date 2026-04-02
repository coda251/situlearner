package com.coda.situlearner.feature.word.quiz.entry.model

import kotlin.time.Instant

internal sealed interface QuizState {
    data object NeedQuiz : QuizState
    data class WaitUntil(val nextQuizDate: Instant) : QuizState
    data object NoWord : QuizState
}

internal fun Instant?.asQuizState(currentDate: Instant): QuizState =
    when {
        this == null -> QuizState.NoWord
        this <= currentDate -> QuizState.NeedQuiz
        else -> QuizState.WaitUntil(this)
    }