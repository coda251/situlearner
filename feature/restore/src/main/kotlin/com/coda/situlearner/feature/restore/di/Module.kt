package com.coda.situlearner.feature.restore.di

import com.coda.situlearner.feature.restore.RestoreViewModel
import com.coda.situlearner.feature.restore.domain.RestoreDataUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val restoreModule = module {
    viewModel { RestoreViewModel(get()) }
    factory {
        RestoreDataUseCase(context = androidContext())
    }
}