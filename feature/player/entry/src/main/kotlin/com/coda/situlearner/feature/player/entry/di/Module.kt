package com.coda.situlearner.feature.player.entry.di

import com.coda.situlearner.feature.player.entry.PlayerSubtitleViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerEntryModule = module {
    viewModel { PlayerSubtitleViewModel(get()) }
}