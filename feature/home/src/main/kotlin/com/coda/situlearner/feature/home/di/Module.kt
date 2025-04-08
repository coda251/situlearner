package com.coda.situlearner.feature.home.di

import com.coda.situlearner.feature.home.explore.collection.di.homeExploreCollectionModule
import com.coda.situlearner.feature.home.explore.library.di.homeExploreLibraryModule
import com.coda.situlearner.feature.home.media.collection.di.homeMediaCollectionModule
import com.coda.situlearner.feature.home.media.library.di.homeMediaLibraryModule
import com.coda.situlearner.feature.home.settings.common.di.homeSettingsCommonModule
import com.coda.situlearner.feature.home.word.book.di.homeWordBookModule
import com.coda.situlearner.feature.home.word.library.di.homeWordLibraryModule

val homeModules = listOf(
    homeExploreCollectionModule,
    homeExploreLibraryModule,
    homeMediaCollectionModule,
    homeMediaLibraryModule,
    homeSettingsCommonModule,
    homeWordLibraryModule,
    homeWordBookModule,
)