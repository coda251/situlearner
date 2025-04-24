package com.coda.situlearner.feature.player.word.di

import com.coda.situlearner.feature.player.word.PlayerWordViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerWordModule = module {
    viewModel { PlayerWordViewModel(get(), get()) }
}