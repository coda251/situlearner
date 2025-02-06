package com.coda.situlearner.feature.word.category.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.category.WordCategoryScreen
import kotlinx.serialization.Serializable

@Serializable
data class WordCategoryRoute(val categoryId: String)

fun NavController.navigateToWordCategory(categoryId: String) {
    navigate(WordCategoryRoute(categoryId = categoryId))
}

fun NavGraphBuilder.wordCategoryScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit
) {
    composable<WordCategoryRoute> {
        WordCategoryScreen(
            onBack = onBack,
            onNavigateToWordDetail = onNavigateToWordDetail
        )
    }
}