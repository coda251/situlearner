package com.coda.situlearner.feature.word.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.word.list.WordListScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordListBaseRoute

@Serializable
data class WordListRoute(
    val wordListType: WordListType,
    val id: String?,
)

fun NavController.navigateToWordList(
    wordListType: WordListType,
    id: String?
) {
    navigate(
        WordListRoute(
            wordListType = wordListType,
            id = id
        )
    )
}

fun NavGraphBuilder.wordListScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToWordEcho: () -> Unit,
    destination: NavGraphBuilder.() -> Unit,
) {
    navigation<WordListBaseRoute>(startDestination = WordListRoute::class) {
        composable<WordListRoute> {
            WordListScreen(
                onBack = onBack,
                onNavigateToWordDetail = onNavigateToWordDetail,
                onNavigateToWordEcho = onNavigateToWordEcho,
            )
        }

        destination()
    }
}