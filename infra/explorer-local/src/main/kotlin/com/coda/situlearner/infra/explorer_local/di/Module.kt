package com.coda.situlearner.infra.explorer_local.di

import com.coda.situlearner.infra.explorer_local.LocalExplorer
import com.coda.situlearner.infra.explorer_local.LocalExplorerImpl
import org.koin.dsl.module

val exploreLocalModule = module {
    factory<LocalExplorer> { LocalExplorerImpl() }
}