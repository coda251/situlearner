package com.coda.situlearner.feature.home.explore.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.coda.situlearner.feature.home.explore.entry.ExploreLibraryScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeExploreBaseRoute

@Serializable
data object HomeExploreEntryRoute

fun NavController.navigateToHomeExploreEntry() {
    navigate(route = HomeExploreBaseRoute)
}

fun NavGraphBuilder.homeExploreEntryScreen(
    onNavigateToCollection: (String) -> Unit,
    onBack: () -> Unit,
    destination: NavGraphBuilder.() -> Unit
) {
    navigation<HomeExploreBaseRoute>(startDestination = HomeExploreEntryRoute) {
        composable<HomeExploreEntryRoute> {
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