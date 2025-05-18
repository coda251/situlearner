package com.coda.situlearner.feature.home.explore.collection.di

import com.coda.situlearner.feature.home.explore.collection.ExploreCollectionViewModel
import com.coda.situlearner.feature.home.explore.collection.WorkerDialogViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeExploreCollectionModule = module {
    viewModel { ExploreCollectionViewModel(get(), get(), get()) }
    viewModel { WorkerDialogViewModel(get(), get()) }
}