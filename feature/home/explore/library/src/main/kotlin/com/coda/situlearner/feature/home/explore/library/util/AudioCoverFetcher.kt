package com.coda.situlearner.feature.home.explore.library.util

import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import com.coda.situlearner.core.model.infra.SourceCollection
import com.coda.situlearner.infra.explorer_local.util.extractBitmapFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AudioCoverFetcher(
    private val path: String
) : Fetcher {

    override suspend fun fetch(): FetchResult? = withContext(Dispatchers.IO) {
        extractBitmapFrom(path)?.let {
            ImageFetchResult(
                image = it.asImage(),
                isSampled = false,
                dataSource = DataSource.DISK
            )
        }
    }

    class Factory : Fetcher.Factory<SourceCollection> {

        override fun create(
            data: SourceCollection,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher? {
            return data.bitmapProviderUrl?.toUri()?.path?.let {
                AudioCoverFetcher(it)
            }
        }
    }
}