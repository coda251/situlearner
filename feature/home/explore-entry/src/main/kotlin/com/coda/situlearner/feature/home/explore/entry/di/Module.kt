package com.coda.situlearner.feature.home.explore.entry.di

import com.coda.situlearner.feature.home.explore.entry.ExploreLibraryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeExploreEntryModule = module {
    viewModel { ExploreLibraryViewModel(get(), get()) }
}