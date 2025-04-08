package com.coda.situlearner.feature.home.word.book.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.home.word.book.WordBookScreen
import kotlinx.serialization.Serializable

@Serializable
data class WordBookRoute(val id: String)

fun NavController.navigateToWordBook(id: String) {
    navigate(route = WordBookRoute(id))
}

fun NavGraphBuilder.wordBookSection(
    onBack: () -> Unit,
    onNavigateToWordList: (WordListType, String) -> Unit,
) {
    composable<WordBookRoute> {
        WordBookScreen(
            onBack = onBack,
            onNavigateToWordList = onNavigateToWordList
        )
    }
}