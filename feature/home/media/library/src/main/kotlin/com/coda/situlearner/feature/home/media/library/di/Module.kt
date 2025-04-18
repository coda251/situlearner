package com.coda.situlearner.feature.home.media.library.di

import com.coda.situlearner.feature.home.media.library.MediaLibraryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeMediaLibraryModule = module {
    viewModel { MediaLibraryViewModel(get()) }
}