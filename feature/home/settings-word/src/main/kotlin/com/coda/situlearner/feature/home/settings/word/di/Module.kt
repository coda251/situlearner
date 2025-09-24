package com.coda.situlearner.feature.home.settings.word.di

import com.coda.situlearner.feature.home.settings.word.SettingsWordViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeSettingsWordModule = module {
    viewModel { SettingsWordViewModel(get(), get()) }
}