package com.coda.situlearner.feature.home.media.collection.di

import com.coda.situlearner.feature.home.media.collection.MediaCollectionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeMediaCollectionModule = module {
    viewModel { MediaCollectionViewModel(get(), get()) }
}