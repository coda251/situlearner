package com.coda.situlearner.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.coda.situlearner.core.model.data.PlaylistType
import com.coda.situlearner.feature.home.navigation.HomeRoute
import com.coda.situlearner.feature.home.navigation.homeScreen
import com.coda.situlearner.feature.player.entry.navigation.PlayerEntryRoute
import com.coda.situlearner.feature.player.entry.navigation.navigateToPlayerEntry
import com.coda.situlearner.feature.player.entry.navigation.playerEntryScreen
import com.coda.situlearner.feature.player.playlist.navigation.navigateToPlaylist
import com.coda.situlearner.feature.player.playlist.navigation.playlistScreen
import com.coda.situlearner.feature.word.detail.navigation.WordDetailRoute
import com.coda.situlearner.feature.word.detail.navigation.navigateToWordDetail
import com.coda.situlearner.feature.word.detail.navigation.wordDetailScreen
import com.coda.situlearner.feature.word.echo.navigation.navigateToWordEcho
import com.coda.situlearner.feature.word.echo.navigation.wordEchoScreen
import com.coda.situlearner.feature.word.edit.navigation.navigateToWordEdit
import com.coda.situlearner.feature.word.edit.navigation.wordEditScreen
import com.coda.situlearner.feature.word.list.navigation.WordListRoute
import com.coda.situlearner.feature.word.list.navigation.navigateToWordList
import com.coda.situlearner.feature.word.list.navigation.wordListScreen
import com.coda.situlearner.feature.word.quiz.navigation.WordQuizRoute
import com.coda.situlearner.feature.word.quiz.navigation.navigateToWordQuiz
import com.coda.situlearner.feature.word.quiz.navigation.wordQuizScreen
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider

@Composable
fun AppNavHost(
    appNavController: NavHostController = rememberNavController(),
) {
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()
    SwitchPlaylistType(playerState, appNavController)

    NavHost(
        navController = appNavController,
        startDestination = HomeRoute,
    ) {
        homeScreen(
            onNavigateToWordList = appNavController::navigateToWordList,
            onNavigateToWordDetail = appNavController::navigateToWordDetail,
            onNavigateToPlayer = appNavController::navigateToPlayerEntry,
            onNavigateToWordQuiz = appNavController::navigateToWordQuiz,
        )

        wordListScreen(
            onBack = appNavController::popBackStack,
            onNavigateToWordDetail = appNavController::navigateToWordDetail,
            onNavigateToWordEcho = appNavController::navigateToWordEcho,
        ) {
            wordEchoScreen(
                onBack = appNavController::popBackStack
            )
        }

        wordDetailScreen(
            onBack = appNavController::popBackStack,
            onNavigateToPlayer = appNavController::navigateToPlayerEntry,
            onNavigateToWordEdit = appNavController::navigateToWordEdit
        ) {
            wordEditScreen(
                onBack = appNavController::popBackStack
            )
        }

        wordQuizScreen(
            onBack = appNavController::popBackStack
        )

        playerEntryScreen(
            onBack = appNavController::popBackStack,
            onNavigateToPlaylist = appNavController::navigateToPlaylist
        ) {
            playlistScreen(
                onBackToPlayer = appNavController::popBackStack,
                onBackToParentOfPlayer = {
                    appNavController.popBackStack(
                        PlayerEntryRoute,
                        inclusive = true
                    )
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
                    it.destination.hasRoute(WordDetailRoute::class) ||
                            it.destination.hasRoute(WordListRoute::class) ||
                            it.destination.hasRoute(WordQuizRoute::class)
                }) PlaylistType.Temporary
            else PlaylistType.Persistent
        }
    }

    LaunchedEffect(currentPlaylistType) {
        playerState.switchPlaylistType(currentPlaylistType)
    }
}