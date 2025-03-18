package com.coda.situlearner.feature.word.category.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.feature.word.category.WordCategoryScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordCategoryBaseRoute

@Serializable
data class WordCategoryRoute(
    val categoryType: WordCategoryType,
    val categoryId: String,
)

fun NavController.navigateToWordCategory(
    categoryType: WordCategoryType,
    categoryId: String
) {
    navigate(
        WordCategoryRoute(
            categoryType = categoryType,
            categoryId = categoryId
        )
    )
}

fun NavGraphBuilder.wordCategoryScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToWordEcho: () -> Unit,
    onNavigateToWordCategory: (WordCategoryType, String) -> Unit,
    destination: NavGraphBuilder.() -> Unit,
) {
    navigation<WordCategoryBaseRoute>(startDestination = WordCategoryRoute::class) {
        composable<WordCategoryRoute> {
            WordCategoryScreen(
                onBack = onBack,
                onNavigateToWordDetail = onNavigateToWordDetail,
                onNavigateToWordEcho = onNavigateToWordEcho,
                onNavigateToWordCategory = onNavigateToWordCategory
            )
        }

        destination()
    }
}