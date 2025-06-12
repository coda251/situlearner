package com.coda.situlearner.feature.home.settings.quiz.di

import com.coda.situlearner.feature.home.settings.quiz.SettingsQuizViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeSettingsQuizModule = module {
    viewModel { SettingsQuizViewModel(get(), get()) }
}