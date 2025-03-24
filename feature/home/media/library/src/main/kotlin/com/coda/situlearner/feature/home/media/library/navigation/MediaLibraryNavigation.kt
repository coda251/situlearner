package com.coda.situlearner.feature.home.media.library.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.feature.home.media.library.MediaLibraryScreen
import kotlinx.serialization.Serializable

@Serializable
data object MediaLibraryBaseRoute

@Serializable
data object MediaLibraryRoute

fun NavController.navigateToMediaLibrary(navOptions: NavOptions) {
    navigate(route = MediaLibraryBaseRoute, navOptions = navOptions)
}

fun NavGraphBuilder.mediaLibrarySection(
    onNavigateToCollection: (MediaCollection) -> Unit,
    onNavigateToExplore: () -> Unit,
    destination: NavGraphBuilder.() -> Unit
) {
    navigation<MediaLibraryBaseRoute>(startDestination = MediaLibraryRoute) {
        composable<MediaLibraryRoute> {
            MediaLibraryScreen(
                onNavigateToCollection = onNavigateToCollection,
                onNavigateToExplore = onNavigateToExplore,
            )
        }

        destination()
    }
}