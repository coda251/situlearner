package com.coda.situlearner.feature.home.explore.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.coda.situlearner.feature.home.explore.entry.ExploreLibraryScreen
import kotlinx.serialization.Serializable

@Serializable
data object ExploreLibraryBaseRoute

@Serializable
data object ExploreLibraryRoute

fun NavController.navigateToExploreLibrary() {
    navigate(route = ExploreLibraryBaseRoute)
}

fun NavGraphBuilder.exploreLibrarySection(
    onNavigateToCollection: (String) -> Unit,
    onBack: () -> Unit,
    destination: NavGraphBuilder.() -> Unit
) {
    navigation<ExploreLibraryBaseRoute>(startDestination = ExploreLibraryRoute) {
        composable<ExploreLibraryRoute> {
            ExploreLibraryScreen(
                onNavigateToCollection = {
                    onNavigateToCollection(it.url)
                },
                onBack = onBack
            )
        }

        destination()
    }
}