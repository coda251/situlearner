package com.coda.situlearner.feature.home.entry.di

import com.coda.situlearner.feature.home.explore.collection.di.homeExploreCollectionModule
import com.coda.situlearner.feature.home.explore.entry.di.homeExploreEntryModule
import com.coda.situlearner.feature.home.media.collection.di.homeMediaCollectionModule
import com.coda.situlearner.feature.home.media.entry.di.homeMediaEntryModule
import com.coda.situlearner.feature.home.settings.chatbot.di.homeSettingsChatbotModule
import com.coda.situlearner.feature.home.settings.entry.di.homeSettingsEntryModule
import com.coda.situlearner.feature.home.word.book.di.homeWordBookModule
import com.coda.situlearner.feature.home.word.entry.di.homeWordEntryModule

val homeEntryModules = listOf(
    homeExploreCollectionModule,
    homeExploreEntryModule,
    homeMediaCollectionModule,
    homeMediaEntryModule,
    homeSettingsChatbotModule,
    homeSettingsEntryModule,
    homeWordBookModule,
    homeWordEntryModule,
)