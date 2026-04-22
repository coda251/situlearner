package com.coda.situlearner.feature.home.media.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.media.edit.EditScreen
import kotlinx.serialization.Serializable

@Serializable
data class HomeMediaEditRoute(val collectionId: String)

fun NavController.navigateToHomeMediaEdit(collectionId: String) {
    navigate(HomeMediaEditRoute(collectionId))
}

fun NavGraphBuilder.homeMediaEditScreen(
    onBack: () -> Unit,
) {
    composable<HomeMediaEditRoute> {
        EditScreen(
            onBack = onBack,
        )
    }
}