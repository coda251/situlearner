package com.coda.situlearner.feature.home.word.library.di

import com.coda.situlearner.feature.home.word.library.WordCategoriesSelectorViewModel
import com.coda.situlearner.feature.home.word.library.WordLibraryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeWordLibraryModule = module {
    viewModel { WordLibraryViewModel(get()) }
    viewModel { WordCategoriesSelectorViewModel(get()) }
}