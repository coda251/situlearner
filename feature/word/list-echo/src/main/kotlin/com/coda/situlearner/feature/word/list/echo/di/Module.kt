package com.coda.situlearner.feature.word.list.echo.di

import com.coda.situlearner.feature.word.list.echo.WordEchoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordListEchoModule = module {
    viewModel { WordEchoViewModel(get()) }
}