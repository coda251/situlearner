package com.coda.situlearner.infra.explorer.local

import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.model.infra.MediaFileFormat
import com.coda.situlearner.core.model.infra.SourceCollection
import com.coda.situlearner.core.model.infra.SourceCollectionWithFiles
import com.coda.situlearner.core.model.infra.SourceFile
import com.coda.situlearner.core.model.infra.SubtitleFileFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.FileMetadata
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.io.File

internal class LocalExplorerImpl : LocalExplorer {

    override fun getSourceCollections(dir: String): Flow<List<SourceCollection>> = flow {
        val path = dir.toPath()

        emit(buildList {
            FileSystem.SYSTEM.listOrNull(path)?.let { paths ->
                paths.forEach {
                    if (it.isDirectory && !it.isHidden) {
                        add(
                            SourceCollection(
                                name = it.name,
                                url = it.asUrl,
                                mediaType = it.mediaType(),
                                bitmapProviderUrl = it.bitmapProviderPath()?.asUrl,
                            )
                        )
                    }
                }
            }
        })
    }.flowOn(Dispatchers.IO)

    override fun getSourceCollectionWithFiles(dir: String): Flow<SourceCollectionWithFiles> = flow {
        val path = dir.toPath()

        val mediaToSubtitle = getMediaToSubtitleFileMapper(path)

        val collection = SourceCollection(
            name = path.name,
            url = path.asUrl,
            mediaType = path.mediaType(),
            bitmapProviderUrl = path.bitmapProviderPath()?.asUrl,
        )

        val files = buildList {
            mediaToSubtitle.entries.forEach {
                it.key.mediaType()?.let { type ->
                    add(
                        SourceFile(
                            name = it.key.nameWithoutExtension,
                            mediaUrl = it.key.asUrl,
                            mediaName = it.key.name,
                            mediaSize = it.key.size,
                            mediaType = type,
                            subtitleName = it.value?.name,
                            subtitleUrl = it.value?.asUrl,
                            subtitleSize = it.value?.size
                        )
                    )
                }
            }
        }

        emit(
            SourceCollectionWithFiles(
                collection = collection,
                files = files
            )
        )
    }.flowOn(Dispatchers.IO)
}

private fun getMediaToSubtitleFileMapper(path: Path): Map<Path, Path?> {
    return FileSystem.SYSTEM.listOrNull(path)?.let { paths ->
        val mediaPaths =
            paths.filter { MediaFileFormat.extensionToType.keys.contains(it.extension) }
        val subtitlePaths = paths.filter {
            SubtitleFileFormat.extensionToFormat.keys.contains(it.extension)
        }

        buildMap {
            mediaPaths.onEach { mediaPath ->
                this[mediaPath] = subtitlePaths.find {
                    it.nameWithoutExtension == mediaPath.nameWithoutExtension
                }
            }
        }
    } ?: emptyMap()
}

private fun Path.mediaType(): MediaType? {
    if (this.isRegularFile) {
        return MediaFileFormat.extensionToType.getOrDefault(this.extension, null)
    }

    if (this.isDirectory) {
        return FileSystem.SYSTEM.listOrNull(this)?.firstNotNullOfOrNull {
            if (it.isRegularFile) it.mediaType()
            else null
        }
    }

    return null
}

private fun Path.bitmapProviderPath(): Path? {
    if (this.isRegularFile) {
        val extension = this.extension
        val type = MediaFileFormat.extensionToType[extension]
        return when (type) {
            MediaType.Audio, MediaType.Video -> this
            else -> null
        }
    }

    if (this.isDirectory) {
        return FileSystem.SYSTEM.listOrNull(this)?.firstNotNullOfOrNull {
            if (it.isRegularFile) it.bitmapProviderPath()
            else null // we don't walk through all sub dirs
        }
    }

    return null
}

private val Path.nameWithoutExtension: String
    get() = name.substringBeforeLast('.')

private val Path.extension: String
    get() = this.name.substringAfterLast('.', missingDelimiterValue = "")

private val Path.metadata: FileMetadata?
    get() = FileSystem.SYSTEM.metadataOrNull(this)

private val Path.isRegularFile: Boolean
    get() = metadata?.isRegularFile == true

private val Path.isDirectory: Boolean
    get() = metadata?.isDirectory == true

private val Path.isHidden: Boolean
    get() = this.name.startsWith(".")

private val Path.size: Long?
    get() = metadata?.size

// currently depend on File to get URL
private val Path.asUrl: String
    get() = File(toString()).toURI().toURL().toString()