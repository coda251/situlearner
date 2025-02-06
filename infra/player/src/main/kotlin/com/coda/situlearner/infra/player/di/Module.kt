package com.coda.situlearner.infra.player.di

import com.coda.situlearner.infra.player.PlayerEngine
import com.coda.situlearner.infra.player.exoplayer.ExoPlayerEngine
import com.coda.situlearner.infra.player.exoplayer.ExoPlaylistManager
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

val playerModule = module {
    factory<PlayerEngine> { (scope: CoroutineScope) ->
        ExoPlayerEngine(get(), ExoPlaylistManager(), scope)
    }
}
