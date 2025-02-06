package com.coda.situlearner.feature.word.category.di

import com.coda.situlearner.feature.word.category.WordCategoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordCategoryModule = module {
    viewModel { WordCategoryViewModel(get(), get()) }
}