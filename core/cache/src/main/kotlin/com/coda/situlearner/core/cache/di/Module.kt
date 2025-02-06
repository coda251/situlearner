package com.coda.situlearner.core.cache.di

import com.coda.situlearner.core.cache.CoverImageCacheManager
import com.coda.situlearner.core.cache.SubtitleCacheManager
import org.koin.dsl.module

val cacheModule = module {
    factory {
        SubtitleCacheManager(get())
    }

    factory {
        CoverImageCacheManager(get())
    }
}
