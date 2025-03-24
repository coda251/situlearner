package com.coda.situlearner.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.feature.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToWordCategory: (WordCategoryType, String) -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToWordQuiz: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToWordCategory = onNavigateToWordCategory,
            onNavigateToWordDetail = onNavigateToWordDetail,
            onNavigateToPlayer = onNavigateToPlayer,
            onNavigateToWordQuiz = onNavigateToWordQuiz
        )
    }
}