package com.coda.situlearner.feature.word.quiz.meaning.di

import com.coda.situlearner.feature.word.quiz.meaning.WordQuizViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordQuizModule = module {
    viewModel { WordQuizViewModel(get(), get()) }
}