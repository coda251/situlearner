package com.coda.situlearner.feature.word.search.di

import com.coda.situlearner.feature.word.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordSearchModule = module {
    viewModel { SearchViewModel(get(), get(), get()) }
}