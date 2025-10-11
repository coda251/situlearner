package com.coda.situlearner.feature.word.list.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.word.list.entry.WordListScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordListBaseRoute

@Serializable
data class WordListEntryRoute(
    val wordListType: WordListType,
    val id: String?,
    val wordProficiencyType: WordProficiencyType?,
)

fun NavController.navigateToWordListEntry(
    wordListType: WordListType,
    id: String?,
    wordProficiencyType: WordProficiencyType?
) {
    navigate(
        WordListEntryRoute(
            wordListType = wordListType,
            id = id,
            wordProficiencyType = wordProficiencyType
        )
    )
}

fun NavGraphBuilder.wordListEntryScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String, WordProficiencyType) -> Unit,
    onNavigateToWordEcho: (WordProficiencyType) -> Unit,
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