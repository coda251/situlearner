package com.coda.situlearner.feature.home.explore.entry.util

import android.content.Context
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import com.coda.situlearner.core.model.infra.MediaFileFormat
import com.coda.situlearner.core.model.infra.SourceCollection
import com.coda.situlearner.core.ui.util.extractBitmapFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class FetcherFactory(
    private val context: Context
) : Fetcher.Factory<SourceCollection> {

    override fun create(
        data: SourceCollection,
        options: Options,
        imageLoader: ImageLoader
    ): Fetcher? {
        return data.bitmapProviderUrl?.toUri()?.path?.let {
            AudioCoverFetcher(it, context)
        }
    }
}

internal class AudioCoverFetcher(
    private val path: String,
    private val context: Context,
) : Fetcher {

    override suspend fun fetch(): FetchResult? = withContext(Dispatchers.IO) {
        val extension = path.substringAfterLast('.', "").lowercase()
        val isMedia = extension in MediaFileFormat.extensionToType.keys
        extractBitmapFrom(context, path, isMedia)?.let {
            ImageFetchResult(
                image = it.asImage(),
                isSampled = false,
                dataSource = DataSource.DISK
            )
        }
    }
}