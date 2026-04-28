package com.coda.situlearner.feature.player.fullscreen.di


import com.coda.situlearner.feature.player.fullscreen.PlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerFullscreenModule = module {
    viewModel { PlayerViewModel(get(), get()) }
}