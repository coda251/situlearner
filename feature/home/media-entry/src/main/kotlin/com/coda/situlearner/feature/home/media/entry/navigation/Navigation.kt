package com.coda.situlearner.feature.home.media.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.feature.home.media.entry.MediaLibraryScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeMediaBaseRoute

@Serializable
data object HomeMediaEntryRoute

fun NavController.navigateToHomeMediaEntry(navOptions: NavOptions) {
    navigate(route = HomeMediaBaseRoute, navOptions = navOptions)
}

fun NavGraphBuilder.homeMediaEntryScreen(
    onNavigateToCollection: (MediaCollection) -> Unit,
    onNavigateToExplore: () -> Unit,
    destination: NavGraphBuilder.() -> Unit
) {
    navigation<HomeMediaBaseRoute>(startDestination = HomeMediaEntryRoute) {
        composable<HomeMediaEntryRoute> {
            MediaLibraryScreen(
                onNavigateToCollection = onNavigateToCollection,
                onNavigateToExplore = onNavigateToExplore,
            )
        }

        destination()
    }
}