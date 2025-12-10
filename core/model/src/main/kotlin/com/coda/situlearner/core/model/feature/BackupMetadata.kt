package com.coda.situlearner.core.model.feature

import kotlinx.serialization.Serializable

@Serializable
data class BackupMetadata(
    val packageName: String,
    val appVersionCode: Long,
    val appVersionName: String,
    val backupDate: String,
    val sdkInt: Int,
    val deviceManufacturer: String,
) {
    companion object {
        const val FILENAME = "metadata.json"
    }
}