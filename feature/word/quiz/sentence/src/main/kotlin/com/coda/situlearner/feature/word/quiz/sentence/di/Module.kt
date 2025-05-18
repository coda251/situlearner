package com.coda.situlearner.feature.word.quiz.sentence.di

import com.coda.situlearner.feature.word.quiz.sentence.QuizSentenceViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val quizSentenceModule = module {
    viewModel { QuizSentenceViewModel(get(), get(), get()) }
}