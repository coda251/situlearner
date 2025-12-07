package com.coda.situlearner.feature.word.detail.relation.di

import com.coda.situlearner.feature.word.detail.relation.WordRelationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wordDetailRelationModule = module {
    viewModel { WordRelationViewModel(get(), get()) }
}