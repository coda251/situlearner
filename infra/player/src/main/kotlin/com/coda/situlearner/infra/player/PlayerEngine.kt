package com.coda.situlearner.infra.player

import kotlinx.coroutines.CoroutineScope

interface PlayerEngine : PlayerState {

    val scope: CoroutineScope

    fun onCreate()

    fun onDestroy()
}