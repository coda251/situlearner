package com.coda.situlearner.core.model.data

import androidx.annotation.Keep

@Keep
enum class QuizDueMode {
    Now,
    Today; // before 24:00
}