package com.coda.situlearner.feature.home.explore.library.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.coda.situlearner.feature.home.explore.library.ExploreLibraryScreen
import kotlinx.serialization.Serializable

@Serializable
data object ExploreLibraryBaseRoute

@Serializable
data object ExploreLibraryRoute

fun NavController.navigateToExploreLibrary(navOptions: NavOptions) {
    navigate(route = ExploreLibraryBaseRoute, navOptions = navOptions)
}

fun NavGraphBuilder.exploreLibrarySection(
    onNavigateToCollection: (String) -> Unit,
    destination: NavGraphBuilder.() -> Unit
) {
    navigation<ExploreLibraryBaseRoute>(startDestination = ExploreLibraryRoute) {
        composable<ExploreLibraryRoute> {
            ExploreLibraryScreen(
                onNavigateToCollection = {
                    onNavigateToCollection(it.url)
                },
            )
        }

        destination()
    }
}