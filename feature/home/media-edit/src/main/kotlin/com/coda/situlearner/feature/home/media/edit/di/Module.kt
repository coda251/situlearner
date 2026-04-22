package com.coda.situlearner.feature.home.media.edit.di

import com.coda.situlearner.feature.home.media.edit.EditViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeMediaEditModule = module {
    viewModel { EditViewModel(get(), get(), get()) }
}