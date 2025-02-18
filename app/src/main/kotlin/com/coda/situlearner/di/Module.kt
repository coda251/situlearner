package com.coda.situlearner.di

import android.content.Context
import com.coda.situlearner.MainActivityViewModel
import com.coda.situlearner.core.cache.di.cacheModule
import com.coda.situlearner.core.cfg.CacheConfig
import com.coda.situlearner.core.data.di.repositoryModule
import com.coda.situlearner.core.database.di.databaseModule
import com.coda.situlearner.core.datastore.di.dataStoreModule
import com.coda.situlearner.feature.home.di.homeModules
import com.coda.situlearner.feature.player.entry.di.playerEntryModule
import com.coda.situlearner.feature.word.category.di.wordCategoryModule
import com.coda.situlearner.feature.word.detail.di.wordDetailModule
import com.coda.situlearner.feature.word.echo.di.wordEchoModule
import com.coda.situlearner.infra.explorer_local.di.exploreLocalModule
import com.coda.situlearner.infra.player.di.playerModule
import com.coda.situlearner.infra.subkit.lang_detector.di.languageDetectorModule
import com.coda.situlearner.infra.subkit.processor.di.processorModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private val activityModule = module {
    viewModel { MainActivityViewModel(get(), get()) }
}

private val configModule = module {
    single {
        CacheConfig(get<Context>().filesDir.path)
    }
}

private val featureModules = listOf(
    playerEntryModule,
    wordCategoryModule,
    wordDetailModule,
    wordEchoModule
) + homeModules

private val infraModules = listOf(
    exploreLocalModule,
    languageDetectorModule,
    processorModule,
    playerModule,
)

val appModules = listOf(
    activityModule,
    cacheModule,
    configModule,
    dataStoreModule,
    databaseModule,
    repositoryModule,
) + infraModules + featureModules