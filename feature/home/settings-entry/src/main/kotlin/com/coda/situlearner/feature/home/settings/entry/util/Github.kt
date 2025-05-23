package com.coda.situlearner.feature.home.settings.entry.util

import com.coda.situlearner.feature.home.settings.entry.model.VersionState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GitHubRelease(
    @SerialName("tag_name") val tagName: String,
    val assets: List<GitHubAsset>
)

@Serializable
internal data class GitHubAsset(
    @SerialName("browser_download_url") val browserDownloadUrl: String
)

private const val GITHUB_API = "https://api.github.com/repos/coda251/situlearner/releases/latest"

internal suspend fun getRelease(client: HttpClient): GitHubRelease = client.get(GITHUB_API).body()

private fun hasUpdate(local: String, remote: String): Boolean {
    // currently, we do not handle x.y.z-alpha (or beta, rc...)
    val localParts = local.split(".")
    val remoteParts = remote.split(".")
    val maxLength = maxOf(localParts.size, remoteParts.size)

    for (i in 0 until maxLength) {
        val localNum = localParts.getOrNull(i)?.toIntOrNull() ?: 0
        val remoteNum = remoteParts.getOrNull(i)?.toIntOrNull() ?: 0

        if (localNum < remoteNum) return true
    }

    return false
}

internal fun GitHubRelease.toVersionState(local: String?): VersionState {
    val remote = this.tagName.trimStart('v')
    val state = VersionState.UpdateAvailable(
        version = remote,
        downloadUrl = this.assets.first().browserDownloadUrl
    )

    if (local == null) return state
    return if (hasUpdate(local, remote)) state
    else VersionState.UpToDate
}