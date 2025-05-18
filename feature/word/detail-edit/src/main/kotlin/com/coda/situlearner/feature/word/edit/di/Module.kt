package com.coda.situlearner.feature.word.edit.di

import com.coda.situlearner.feature.word.edit.WordEditViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordEditModule = module {
    viewModel { WordEditViewModel(get(), get()) }
}