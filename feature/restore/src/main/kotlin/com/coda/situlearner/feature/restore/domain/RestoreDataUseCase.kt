package com.coda.situlearner.feature.restore.domain

import android.content.Context
import android.net.Uri
import com.coda.situlearner.core.model.feature.BackupMetadata
import com.coda.situlearner.feature.restore.model.RestoreState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

internal class RestoreDataUseCase(private val context: Context) {
    suspend operator fun invoke(uri: Uri): RestoreState = withContext(Dispatchers.IO) {
        val tempFile = File(
            context.cacheDir,
            "temp_backup_${System.currentTimeMillis()}.zip"
        )

        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext RestoreState.Error("Cannot open backup file")

            val metadata = ZipFile(tempFile).use {
                readMetadata(it)
            } ?: return@withContext RestoreState.Error("Invalid metadata file")

            val error = validateMetadata(metadata)
            if (error != null) {
                return@withContext error
            }

            FileInputStream(tempFile).use { input ->
                ZipInputStream(input).use { zip ->
                    restoreFromZip(context, zip)
                }
            }

            RestoreState.Success
        } catch (e: Exception) {
            RestoreState.Error(e.message ?: "Unknown error")
        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }

    private fun validateMetadata(metadata: BackupMetadata): RestoreState.Error? {
        if (metadata.packageName != context.packageName) {
            return RestoreState.Error("This backup is not for this app")
        }

        val currentVersion = context.packageManager
            .getPackageInfo(context.packageName, 0)
            .longVersionCode

        if (metadata.appVersionCode != currentVersion) {
            return RestoreState.Error(
                "Backup is from app version ${metadata.appVersionCode}, " +
                        "but the current app version is $currentVersion. " +
                        "Please update the app to proceed."
            )
        }

        return null
    }

    private fun readMetadata(zipFile: ZipFile): BackupMetadata? {
        val entry = zipFile.getEntry(BackupMetadata.FILENAME) ?: return null
        zipFile.getInputStream(entry).use { entryStream ->
            return entryStream.bufferedReader(Charsets.UTF_8).use {
                Json.decodeFromString(it.readText())
            }
        }
    }

    private fun restoreFromZip(
        context: Context,
        zip: ZipInputStream
    ) {
        var entry: ZipEntry?
        while (zip.nextEntry.also { entry = it } != null) {
            val name = entry!!.name

            // note that the absolute url recorded in zip file
            // may be not accessible in new device
            when {
                name.startsWith("databases/") -> {
                    restoreByMapping(
                        root = context.getDatabasePath("dummy").parentFile!!,
                        relativePath = name.removePrefix("databases/"),
                        zip = zip,
                    )
                }

                name.startsWith("files/") -> {
                    restoreByMapping(
                        root = context.filesDir,
                        relativePath = name.removePrefix("files/"),
                        zip = zip,
                    )
                }

                else -> {}
            }

            zip.closeEntry()
        }
    }

    private fun restoreByMapping(
        root: File,
        relativePath: String,
        zip: ZipInputStream,
    ) {
        val target = File(root, relativePath)

        // check zip slip
        val canonicalRoot = root.canonicalFile
        val canonicalTarget = target.canonicalFile
        if (!canonicalTarget.path.startsWith(canonicalRoot.path)) {
            throw SecurityException("Zip entry is outside target dir: $relativePath")
        }

        // is dir
        if (relativePath.endsWith("/")) {
            target.mkdirs()
            return
        }
        target.parentFile?.mkdirs()
        FileOutputStream(target).use { out ->
            zip.copyTo(out)
        }
    }
}