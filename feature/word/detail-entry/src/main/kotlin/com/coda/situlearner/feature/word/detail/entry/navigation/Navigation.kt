package com.coda.situlearner.feature.word.detail.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.feature.word.detail.entry.WordDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordDetailBaseRoute

@Serializable
data class WordDetailEntryRoute(
    val wordId: String,
    val wordProficiencyType: WordProficiencyType?,
)

fun NavController.navigateToWordDetailEntry(
    fromWordId: String,
    wordProficiencyType: WordProficiencyType?,
) {
    navigate(
        WordDetailEntryRoute(
            wordId = fromWordId,
            wordProficiencyType = wordProficiencyType
        )
    )
}

fun NavGraphBuilder.wordDetailEntryScreen(
    onBack: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToWordEdit: (String) -> Unit,
    onNavigateToWordRelation: (String) -> Unit,
    destination: NavGraphBuilder.() -> Unit,
) {
    navigation<WordDetailBaseRoute>(startDestination = WordDetailEntryRoute::class) {
        composable<WordDetailEntryRoute> {
            WordDetailScreen(
                onBack = onBack,
                onNavigateToPlayer = onNavigateToPlayer,
                onNavigateToWordEdit = onNavigateToWordEdit,
                onNavigateToWordRelation = onNavigateToWordRelation
            )
        }

        destination()
    }
}