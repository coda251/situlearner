package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.model.data.AppVersionState
import kotlinx.coroutines.flow.StateFlow

interface AppVersionRepository {

    val currentVersion: String

    val appVersionState: StateFlow<AppVersionState>

    suspend fun checkAppUpdate()
}