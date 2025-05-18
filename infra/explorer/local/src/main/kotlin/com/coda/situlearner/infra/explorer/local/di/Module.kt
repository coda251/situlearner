package com.coda.situlearner.infra.explorer.local.di

import com.coda.situlearner.infra.explorer.local.LocalExplorer
import com.coda.situlearner.infra.explorer.local.LocalExplorerImpl
import org.koin.dsl.module

val exploreLocalModule = module {
    factory<LocalExplorer> { LocalExplorerImpl() }
}