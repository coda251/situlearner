package com.coda.situlearner.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToWordCategory: (String) -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToPlayer: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToWordCategory = onNavigateToWordCategory,
            onNavigateToWordDetail = onNavigateToWordDetail,
            onNavigateToPlayer = onNavigateToPlayer
        )
    }
}