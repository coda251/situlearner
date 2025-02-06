package com.coda.situlearner.feature.word.detail.di

import com.coda.situlearner.feature.word.detail.WordDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordDetailModule = module {
    viewModel { WordDetailViewModel(get(), get()) }
}