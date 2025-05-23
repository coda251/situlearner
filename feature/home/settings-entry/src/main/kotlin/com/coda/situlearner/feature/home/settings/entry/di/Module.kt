package com.coda.situlearner.feature.home.settings.entry.di

import com.coda.situlearner.feature.home.settings.entry.SettingsCommonViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeSettingsEntryModule = module {
    viewModel { SettingsCommonViewModel(get(), get(), get()) }
}