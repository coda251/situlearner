package com.coda.situlearner.feature.home.explore.collection.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.explore.collection.ExploreCollectionScreen
import kotlinx.serialization.Serializable

@Serializable
data class ExploreCollectionRoute(val url: String)

fun NavController.navigateToExploreCollection(url: String) {
    navigate(ExploreCollectionRoute(url))
}

fun NavGraphBuilder.exploreCollectionSection(onBack: () -> Unit) {
    composable<ExploreCollectionRoute> {
        ExploreCollectionScreen(onBack = onBack)
    }
}