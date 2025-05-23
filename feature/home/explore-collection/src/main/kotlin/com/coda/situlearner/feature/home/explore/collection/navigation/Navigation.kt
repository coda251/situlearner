package com.coda.situlearner.feature.home.explore.collection.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.explore.collection.ExploreCollectionScreen
import kotlinx.serialization.Serializable

@Serializable
data class HomeExploreCollectionRoute(val url: String)

fun NavController.navigateToHomeExploreCollection(url: String) {
    navigate(HomeExploreCollectionRoute(url))
}

fun NavGraphBuilder.homeExploreCollectionScreen(onBack: () -> Unit) {
    composable<HomeExploreCollectionRoute> {
        ExploreCollectionScreen(onBack = onBack)
    }
}