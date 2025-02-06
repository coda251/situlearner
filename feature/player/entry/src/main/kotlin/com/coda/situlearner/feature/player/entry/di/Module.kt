package com.coda.situlearner.feature.player.entry.di

import com.coda.situlearner.feature.player.entry.PlayerSubtitleViewModel
import com.coda.situlearner.feature.player.entry.PlayerWordBottomSheetRoute
import com.coda.situlearner.feature.player.entry.PlayerWordViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerEntryModule = module {
    viewModel { (route: PlayerWordBottomSheetRoute) -> PlayerWordViewModel(route, get()) }
    viewModel { PlayerSubtitleViewModel(get()) }
}