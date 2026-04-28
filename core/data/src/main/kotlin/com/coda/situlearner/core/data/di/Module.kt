package com.coda.situlearner.core.data.di

import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.data.repository.AppVersionRepository
import com.coda.situlearner.core.data.repository.GithubAppVersionRepository
import com.coda.situlearner.core.data.repository.LocalAiStateRepository
import com.coda.situlearner.core.data.repository.LocalMediaRepository
import com.coda.situlearner.core.data.repository.LocalPlayerStateRepository
import com.coda.situlearner.core.data.repository.LocalUserPreferenceRepository
import com.coda.situlearner.core.data.repository.LocalWordRepository
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.data.repository.PlayerStateRepository
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.data.repository.WordRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<MediaRepository> {
        LocalMediaRepository(get(), get(), get())
    }

    single<WordRepository> {
        LocalWordRepository(get(), get(), get(), get())
    }

    single<UserPreferenceRepository> {
        LocalUserPreferenceRepository(get())
    }

    single<PlayerStateRepository> {
        LocalPlayerStateRepository(get())
    }

    single<AiStateRepository> {
        LocalAiStateRepository(get())
    }

    single<AppVersionRepository> {
        GithubAppVersionRepository(get(), get(named("versionName")))
    }
}