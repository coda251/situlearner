package com.coda.situlearner.feature.home.word.book.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.home.word.book.WordBookScreen
import kotlinx.serialization.Serializable

@Serializable
data class HomeWordBookRoute(val id: String)

fun NavController.navigateToHomeWordBook(id: String) {
    navigate(route = HomeWordBookRoute(id))
}

fun NavGraphBuilder.homeWordBookScreen(
    onBack: () -> Unit,
    onNavigateToWordList: (WordListType, String) -> Unit,
) {
    composable<HomeWordBookRoute> {
        WordBookScreen(
            onBack = onBack,
            onNavigateToWordList = onNavigateToWordList
        )
    }
}