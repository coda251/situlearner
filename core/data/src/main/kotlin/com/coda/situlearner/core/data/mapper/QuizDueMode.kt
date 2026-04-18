package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.QuizDueModeProto
import com.coda.situlearner.core.model.data.QuizDueMode

internal fun QuizDueMode.asProto() = when(this) {
    QuizDueMode.Now -> QuizDueModeProto.QUIZ_DUE_MODE_NOW
    QuizDueMode.Today -> QuizDueModeProto.QUIZ_DUE_MODE_TODAY
}

internal fun QuizDueModeProto.asExternalModel() = when(this) {
    QuizDueModeProto.QUIZ_DUE_MODE_NOW, QuizDueModeProto.UNRECOGNIZED -> QuizDueMode.Now
    QuizDueModeProto.QUIZ_DUE_MODE_TODAY -> QuizDueMode.Today
}