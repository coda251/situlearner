package com.coda.situlearner.feature.home.settings.entry.domain

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import com.coda.situlearner.core.cfg.AppConfig.ROOM_DATABASE_FILENAME
import com.coda.situlearner.core.database.helper.DatabaseHelper
import com.coda.situlearner.core.model.feature.BackupMetadata
import com.coda.situlearner.feature.home.settings.entry.model.ExportState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.time.Clock

internal class ExportDataUseCase(
    private val context: Context,
    private val dbHelper: DatabaseHelper,
) {
    companion object {
        private const val ZIP_FILENAME = "SituLearner.zip"
    }

    suspend operator fun invoke(uri: Uri): ExportState = withContext(Dispatchers.IO) {
        try {
            dbHelper.prepareForBackup()

            val dir = DocumentFile.fromTreeUri(context, uri)
                ?: return@withContext ExportState.Error("Invalid directory")

            dir.findFile(ZIP_FILENAME)?.delete()
            val zipFile = dir.createFile(
                "application/zip",
                ZIP_FILENAME

            ) ?: return@withContext ExportState.Error("Failed to create zip file")

            context.contentResolver.openOutputStream(zipFile.uri)?.use { out ->
                ZipOutputStream(out).use {
                    exportMetadata(context.buildMetadata(), it)
                    exportDatabase(context, it)
                    exportFiles(context, it)
                }
            } ?: return@withContext ExportState.Error("Failed to open output stream")
            ExportState.Success
        } catch (e: Exception) {
            ExportState.Error(e.message ?: "Unknown error")
        }
    }

    private fun exportMetadata(
        metadata: BackupMetadata,
        zip: ZipOutputStream
    ) {
        val json = Json.encodeToString(metadata)
        val entry = ZipEntry(BackupMetadata.FILENAME)
        zip.putNextEntry(entry)
        json.toByteArray(Charsets.UTF_8).let { zip.write(it) }
        zip.closeEntry()
    }

    private fun exportDatabase(
        context: Context,
        zip: ZipOutputStream
    ) {
        val dbFile = context.getDatabasePath(ROOM_DATABASE_FILENAME)
        if (dbFile.exists()) {
            val dbDirName = dbFile.parentFile?.name ?: "databases"
            addFileToZip(
                zip = zip,
                file = dbFile,
                entryName = "$dbDirName/${dbFile.name}"
            )
        }
    }

    private fun exportFiles(
        context: Context,
        zip: ZipOutputStream
    ) {
        val dir = context.filesDir
        if (dir.exists()) {
            addDirectoryToZip(
                zip = zip,
                dir = dir,
                basePath = dir.name
            )
        }
    }

    private fun addFileToZip(
        zip: ZipOutputStream,
        file: File,
        entryName: String
    ) {
        zip.putNextEntry(ZipEntry(entryName))
        file.inputStream().use { it.copyTo(zip) }
        zip.closeEntry()
    }

    private fun addDirectoryToZip(
        zip: ZipOutputStream,
        dir: File,
        basePath: String
    ) {
        dir.listFiles()?.forEach { file ->
            val entryPath = "$basePath/${file.name}"
            if (file.isDirectory) {
                zip.putNextEntry(ZipEntry("$entryPath/"))
                zip.closeEntry()
                addDirectoryToZip(zip, file, entryPath)
            } else {
                addFileToZip(zip, file, entryPath)
            }
        }
    }
}

private fun Context.buildMetadata(): BackupMetadata {
    val pm = this.packageManager
    val pkgInfo = pm.getPackageInfo(this.packageName, 0)
    return BackupMetadata(
        packageName = pkgInfo.packageName,
        appVersionCode = pkgInfo.longVersionCode,
        appVersionName = pkgInfo.versionName ?: "",
        backupDate = Clock.System.now().toString(),
        sdkInt = Build.VERSION.SDK_INT,
        deviceManufacturer = Build.MANUFACTURER,
    )
}