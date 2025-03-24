package com.coda.situlearner.feature.word.quiz.di

import com.coda.situlearner.feature.word.quiz.WordQuizViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordQuizModule = module {
    viewModel { WordQuizViewModel(get(), get()) }
}