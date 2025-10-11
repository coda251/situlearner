package com.coda.situlearner.feature.word.list.echo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.feature.word.list.echo.WordEchoScreen
import kotlinx.serialization.Serializable

@Serializable
data class WordListEchoRoute(
    val wordProficiencyType: WordProficiencyType
)

fun NavController.navigateToWordListEcho(
    wordProficiencyType: WordProficiencyType
) {
    navigate(WordListEchoRoute(wordProficiencyType))
}

fun NavGraphBuilder.wordListEchoScreen(
    onBack: () -> Unit,
) {
    composable<WordListEchoRoute> {
        WordEchoScreen(
            onBack = onBack,
        )
    }
}