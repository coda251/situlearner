package com.coda.situlearner.di

import android.content.Context
import com.coda.situlearner.MainActivityViewModel
import com.coda.situlearner.core.cache.di.cacheModule
import com.coda.situlearner.core.cfg.CacheConfig
import com.coda.situlearner.core.data.di.repositoryModule
import com.coda.situlearner.core.database.di.databaseModule
import com.coda.situlearner.core.datastore.di.dataStoreModule
import com.coda.situlearner.core.network.di.networkModule
import com.coda.situlearner.feature.home.entry.di.homeEntryModules
import com.coda.situlearner.feature.player.entry.di.playerEntryModule
import com.coda.situlearner.feature.player.word.di.playerWordModule
import com.coda.situlearner.feature.restore.di.restoreModule
import com.coda.situlearner.feature.word.detail.entry.di.wordDetailEntryModule
import com.coda.situlearner.feature.word.detail.relation.di.wordDetailRelationModule
import com.coda.situlearner.feature.word.edit.di.wordDetailEditModule
import com.coda.situlearner.feature.word.list.echo.di.wordListEchoModule
import com.coda.situlearner.feature.word.list.entry.di.wordListEntryModule
import com.coda.situlearner.feature.word.quiz.entry.di.wordQuizEntryModule
import com.coda.situlearner.feature.word.quiz.meaning.di.wordQuizMeaningModule
import com.coda.situlearner.feature.word.quiz.sentence.di.wordQuizTranslationModule
import com.coda.situlearner.infra.explorer.local.di.exploreLocalModule
import com.coda.situlearner.infra.player.di.playerModule
import com.coda.situlearner.infra.subkit.processor.di.processorModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private val activityModule = module {
    viewModel { MainActivityViewModel(get()) }
}

private val configModule = module {
    single {
        CacheConfig(get<Context>().filesDir.path)
    }
}

private val featureModules = listOf(
    playerEntryModule,
    playerWordModule,
    restoreModule,
    wordDetailEditModule,
    wordDetailEntryModule,
    wordDetailRelationModule,
    wordListEchoModule,
    wordListEntryModule,
    wordQuizEntryModule,
    wordQuizMeaningModule,
    wordQuizTranslationModule
) + homeEntryModules

private val infraModules = listOf(
    exploreLocalModule,
    processorModule,
    playerModule,
)

val appModules = listOf(
    activityModule,
    cacheModule,
    configModule,
    dataStoreModule,
    databaseModule,
    networkModule,
    repositoryModule,
) + infraModules + featureModules