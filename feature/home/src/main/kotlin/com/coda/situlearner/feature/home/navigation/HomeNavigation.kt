package com.coda.situlearner.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToWordList: (WordListType, String?) -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToWordQuiz: () -> Unit,
    onNavigateToQuizSentence: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToWordList = onNavigateToWordList,
            onNavigateToWordDetail = onNavigateToWordDetail,
            onNavigateToPlayer = onNavigateToPlayer,
            onNavigateToWordQuiz = onNavigateToWordQuiz,
            onNavigateToQuizSentence = onNavigateToQuizSentence
        )
    }
}