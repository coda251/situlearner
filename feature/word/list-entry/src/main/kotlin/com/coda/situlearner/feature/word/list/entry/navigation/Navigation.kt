package com.coda.situlearner.feature.word.list.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.word.list.entry.WordListScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordListBaseRoute

@Serializable
data class WordListEntryRoute(
    val wordListType: WordListType,
    val id: String?,
)

fun NavController.navigateToWordListEntry(
    wordListType: WordListType,
    id: String?
) {
    navigate(
        WordListEntryRoute(
            wordListType = wordListType,
            id = id
        )
    )
}

fun NavGraphBuilder.wordListEntryScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToWordEcho: () -> Unit,
    destination: NavGraphBuilder.() -> Unit,
) {
    navigation<WordListBaseRoute>(startDestination = WordListEntryRoute::class) {
        composable<WordListEntryRoute> {
            WordListScreen(
                onBack = onBack,
                onNavigateToWordDetail = onNavigateToWordDetail,
                onNavigateToWordEcho = onNavigateToWordEcho,
            )
        }

        destination()
    }
}