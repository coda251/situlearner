package com.coda.situlearner.feature.word.detail.entry.di

import com.coda.situlearner.feature.word.detail.entry.WordDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordDetailEntryModule = module {
    viewModel { WordDetailViewModel(get(), get()) }
}