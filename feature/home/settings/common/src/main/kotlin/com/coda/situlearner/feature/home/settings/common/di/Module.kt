package com.coda.situlearner.feature.home.settings.common.di

import com.coda.situlearner.feature.home.settings.common.SettingsCommonViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeSettingsCommonModule = module {
    viewModel { SettingsCommonViewModel(get(), get()) }
}