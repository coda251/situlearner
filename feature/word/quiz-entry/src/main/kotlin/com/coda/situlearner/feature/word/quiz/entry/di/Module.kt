package com.coda.situlearner.feature.word.quiz.entry.di

import com.coda.situlearner.feature.word.quiz.entry.EntryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val quizEntryModule = module {
    viewModel { EntryViewModel(get(), get(), get()) }
}