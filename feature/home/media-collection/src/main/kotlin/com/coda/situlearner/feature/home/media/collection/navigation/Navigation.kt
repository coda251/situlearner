package com.coda.situlearner.feature.home.media.collection.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.feature.home.media.collection.MediaCollectionScreen
import kotlinx.serialization.Serializable

@Serializable
data class HomeMediaCollectionRoute(val collectionId: String)

fun NavController.navigateToHomeMediaCollection(collection: MediaCollection) {
    navigate(route = HomeMediaCollectionRoute(collection.id))
}

fun NavGraphBuilder.homeMediaCollectionScreen(onBack: () -> Unit) {
    composable<HomeMediaCollectionRoute> {
        MediaCollectionScreen(
            onBack = onBack
        )
    }
}