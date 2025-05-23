package com.coda.situlearner.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.coda.situlearner.core.model.data.PlaylistType
import com.coda.situlearner.feature.home.entry.navigation.HomeEntryRoute
import com.coda.situlearner.feature.home.entry.navigation.homeEntryScreen
import com.coda.situlearner.feature.player.entry.navigation.PlayerEntryRoute
import com.coda.situlearner.feature.player.entry.navigation.navigateToPlayerEntry
import com.coda.situlearner.feature.player.entry.navigation.playerEntryScreen
import com.coda.situlearner.feature.player.playlist.navigation.navigateToPlayerPlaylist
import com.coda.situlearner.feature.player.playlist.navigation.playerPlaylistScreen
import com.coda.situlearner.feature.player.word.navigation.navigateToPlayerWord
import com.coda.situlearner.feature.player.word.navigation.playerWordBottomSheet
import com.coda.situlearner.feature.word.detail.entry.navigation.WordDetailBaseRoute
import com.coda.situlearner.feature.word.detail.entry.navigation.navigateToWordDetailEntry
import com.coda.situlearner.feature.word.detail.entry.navigation.wordDetailEntryScreen
import com.coda.situlearner.feature.word.edit.navigation.navigateToWordDetailEdit
import com.coda.situlearner.feature.word.edit.navigation.wordDetailEditScreen
import com.coda.situlearner.feature.word.list.echo.navigation.navigateToWordListEcho
import com.coda.situlearner.feature.word.list.echo.navigation.wordListEchoScreen
import com.coda.situlearner.feature.word.list.entry.navigation.WordListBaseRoute
import com.coda.situlearner.feature.word.list.entry.navigation.navigateToWordListEntry
import com.coda.situlearner.feature.word.list.entry.navigation.wordListEntryScreen
import com.coda.situlearner.feature.word.quiz.entry.navigation.WordQuizBaseRoute
import com.coda.situlearner.feature.word.quiz.entry.navigation.navigateToWordQuizEntry
import com.coda.situlearner.feature.word.quiz.entry.navigation.wordQuizEntryScreen
import com.coda.situlearner.feature.word.quiz.meaning.navigation.navigateToWordQuizMeaning
import com.coda.situlearner.feature.word.quiz.meaning.navigation.wordQuizMeaningScreen
import com.coda.situlearner.feature.word.quiz.sentence.navigation.navigateToWordQuizTranslation
import com.coda.situlearner.feature.word.quiz.sentence.navigation.wordQuizTranslationScreen
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider

@Composable
fun AppNavHost(
    appNavController: NavHostController = rememberNavController(),
) {
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()
    SwitchPlaylistType(playerState, appNavController)

    // NOTE: may seek for a more elegant way to get result from back navigation event
    var resetTokenFlag by remember { mutableIntStateOf(1) }

    NavHost(
        navController = appNavController,
        startDestination = HomeEntryRoute,
    ) {
        homeEntryScreen(
            onNavigateToWordList = appNavController::navigateToWordListEntry,
            onNavigateToWordDetail = appNavController::navigateToWordDetailEntry,
            onNavigateToPlayer = appNavController::navigateToPlayerEntry,
            onNavigateToWordQuiz = appNavController::navigateToWordQuizEntry,
        )

        wordListEntryScreen(
            onBack = appNavController::popBackStack,
            onNavigateToWordDetail = appNavController::navigateToWordDetailEntry,
            onNavigateToWordEcho = appNavController::navigateToWordListEcho,
        ) {
            wordListEchoScreen(
                onBack = appNavController::popBackStack
            )
        }

        wordDetailEntryScreen(
            onBack = appNavController::popBackStack,
            onNavigateToPlayer = appNavController::navigateToPlayerEntry,
            onNavigateToWordEdit = appNavController::navigateToWordDetailEdit
        ) {
            wordDetailEditScreen(
                onBack = appNavController::popBackStack
            )
        }

        wordQuizEntryScreen(
            onBack = appNavController::popBackStack,
            onNavigateToMeaning = appNavController::navigateToWordQuizMeaning,
            onNavigateToTranslation = appNavController::navigateToWordQuizTranslation
        ) {
            wordQuizMeaningScreen(
                onBack = appNavController::popBackStack
            )

            wordQuizTranslationScreen(
                onBack = appNavController::popBackStack,
                onNavigateToWordDetail = appNavController::navigateToWordDetailEntry
            )
        }

        playerEntryScreen(
            resetTokenFlagProvider = { resetTokenFlag },
            onBack = appNavController::popBackStack,
            onNavigateToPlaylist = appNavController::navigateToPlayerPlaylist,
            onNavigateToPlayerWord = appNavController::navigateToPlayerWord
        ) {
            playerPlaylistScreen(
                onBackToPlayer = appNavController::popBackStack,
                onBackToParentOfPlayer = {
                    appNavController.popBackStack(
                        PlayerEntryRoute,
                        inclusive = true
                    )
                }
            )
            playerWordBottomSheet(
                onBack = {
                    resetTokenFlag = -resetTokenFlag
                    appNavController.popBackStack()
                }
            )
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun SwitchPlaylistType(
    playerState: PlayerState,
    navController: NavController
) {
    val navBackStack by navController.currentBackStack.collectAsStateWithLifecycle()

    val currentPlaylistType by remember {
        derivedStateOf {
            if (navBackStack.any {
                    it.destination.hasRoute(WordDetailBaseRoute::class) ||
                            it.destination.hasRoute(WordListBaseRoute::class) ||
                            it.destination.hasRoute(WordQuizBaseRoute::class)
                }) PlaylistType.Temporary
            else PlaylistType.Persistent
        }
    }

    LaunchedEffect(currentPlaylistType) {
        playerState.switchPlaylistType(currentPlaylistType)
    }
}