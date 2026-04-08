package com.coda.situlearner.feature.home.settings.theme.di

import com.coda.situlearner.feature.home.settings.theme.SettingsThemeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeSettingsThemeModule = module {
    viewModel { SettingsThemeViewModel(get()) }
}