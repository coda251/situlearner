package com.coda.situlearner.feature.home.word.book.di

import com.coda.situlearner.feature.home.word.book.WordBookViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeWordBookModule = module {
    viewModel { WordBookViewModel(get(), get()) }
}