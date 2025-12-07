package com.coda.situlearner.feature.word.detail.relation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.detail.relation.WordRelationScreen
import kotlinx.serialization.Serializable

@Serializable
data class WordDetailRelationRoute(
    val wordId: String
)

fun NavController.navigateToWordDetailRelation(
    wordId: String
) {
    navigate(WordDetailRelationRoute(wordId))
}

fun NavGraphBuilder.wordDetailRelationScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit
) {
    composable<WordDetailRelationRoute> {
        WordRelationScreen(
            onBack = onBack,
            onNavigateToWordDetail = onNavigateToWordDetail
        )
    }
}