package com.coda.situlearner.feature.home.entry.di

import com.coda.situlearner.feature.home.explore.collection.di.homeExploreCollectionModule
import com.coda.situlearner.feature.home.explore.entry.di.homeExploreLibraryModule
import com.coda.situlearner.feature.home.media.collection.di.homeMediaCollectionModule
import com.coda.situlearner.feature.home.media.entry.di.homeMediaLibraryModule
import com.coda.situlearner.feature.home.settings.chatbot.di.homeSettingsChatbotModule
import com.coda.situlearner.feature.home.settings.entry.di.homeSettingsCommonModule
import com.coda.situlearner.feature.home.word.book.di.homeWordBookModule
import com.coda.situlearner.feature.home.word.entry.di.homeWordLibraryModule

val homeModules = listOf(
    homeExploreCollectionModule,
    homeExploreLibraryModule,
    homeMediaCollectionModule,
    homeMediaLibraryModule,
    homeSettingsCommonModule,
    homeSettingsChatbotModule,
    homeWordLibraryModule,
    homeWordBookModule,
)