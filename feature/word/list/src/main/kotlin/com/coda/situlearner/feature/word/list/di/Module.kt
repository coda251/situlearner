package com.coda.situlearner.feature.word.list.di

import com.coda.situlearner.feature.word.list.WordListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordListModule = module {
    viewModel { WordListViewModel(get(), get()) }
}