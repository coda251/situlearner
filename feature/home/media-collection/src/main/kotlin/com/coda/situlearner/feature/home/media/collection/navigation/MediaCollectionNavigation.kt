package com.coda.situlearner.feature.home.media.collection.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.feature.home.media.collection.MediaCollectionScreen
import kotlinx.serialization.Serializable

@Serializable
data class MediaCollectionRoute(val collectionId: String)

fun NavController.navigateToMediaCollection(collection: MediaCollection) {
    navigate(route = MediaCollectionRoute(collection.id))
}

fun NavGraphBuilder.mediaCollectionSection(onBack: () -> Unit) {
    composable<MediaCollectionRoute> {
        MediaCollectionScreen(
            onBack = onBack
        )
    }
}