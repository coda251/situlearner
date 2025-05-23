package com.coda.situlearner.feature.word.list.entry.di

import com.coda.situlearner.feature.word.list.entry.WordListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordListEntryModule = module {
    viewModel { WordListViewModel(get(), get()) }
}