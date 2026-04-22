package com.coda.situlearner.feature.home.media.edit.di

import com.coda.situlearner.feature.home.media.edit.EditViewModel
import com.coda.situlearner.feature.home.media.edit.domain.ExtractBitmapUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeMediaEditModule = module {
    viewModel { EditViewModel(get(), get(), get()) }
    factory { ExtractBitmapUseCase(androidContext()) }
}