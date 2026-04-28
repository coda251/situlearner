package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.data.util.getRelease
import com.coda.situlearner.core.data.util.toVersionState
import com.coda.situlearner.core.model.data.AppVersionState
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.withContext

class GithubAppVersionRepository(
    private val client: HttpClient,
    override val currentVersion: String
) : AppVersionRepository {

    private val _appVersionState = MutableStateFlow<AppVersionState>(AppVersionState.NotChecked)
    override val appVersionState = _appVersionState.asStateFlow()

    override suspend fun checkAppUpdate() {
        val previous = _appVersionState.getAndUpdate { current ->
            when (current) {
                is AppVersionState.NotChecked,
                is AppVersionState.Failed -> AppVersionState.Loading

                else -> current
            }
        }
        if (previous !is AppVersionState.NotChecked && previous !is AppVersionState.Failed) {
            return
        }

        _appVersionState.value = withContext(Dispatchers.IO) {
            try {
                getRelease(client).toVersionState(currentVersion)
            } catch (_: Exception) {
                AppVersionState.Failed
            }
        }
    }
}