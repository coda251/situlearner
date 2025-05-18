package com.coda.situlearner.feature.home.word.entry.di

import com.coda.situlearner.feature.home.word.entry.WordLibraryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeWordLibraryModule = module {
    viewModel { WordLibraryViewModel(get(), get()) }
}