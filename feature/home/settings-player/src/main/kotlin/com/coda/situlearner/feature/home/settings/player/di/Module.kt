package com.coda.situlearner.feature.home.settings.player.di

import com.coda.situlearner.feature.home.settings.player.SettingsPlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeSettingsPlayerModule = module {
    viewModel { SettingsPlayerViewModel(get()) }
}