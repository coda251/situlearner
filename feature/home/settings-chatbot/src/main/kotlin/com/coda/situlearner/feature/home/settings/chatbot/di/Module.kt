package com.coda.situlearner.feature.home.settings.chatbot.di

import com.coda.situlearner.feature.home.settings.chatbot.SettingsChatbotViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeSettingsChatbotModule = module {
    viewModel { SettingsChatbotViewModel(get()) }
}