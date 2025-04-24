package com.coda.situlearner.feature.player.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider

@Composable
internal fun PlayerEntryScreen(
    resetTokenFlag: Int,
    onBack: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    onNavigateToPlayerWord: (Token, Subtitle, Language, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()
    PlayerEntryScreen(
        playerState = playerState,
        resetTokenFlag = resetTokenFlag,
        onBack = onBack,
        onNavigateToPlaylist = onNavigateToPlaylist,
        onNavigateToPlayerWord = onNavigateToPlayerWord,
        modifier = modifier
    )
}

@Composable
private fun PlayerEntryScreen(
    playerState: PlayerState,
    resetTokenFlag: Int,
    onBack: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    onNavigateToPlayerWord: (Token, Subtitle, Language, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val playlist by playerState.playlist.collectAsStateWithLifecycle()

    val isPlaylistEmpty by remember { derivedStateOf { playlist.isEmpty() } }
    LaunchedEffect(isPlaylistEmpty) {
        if (isPlaylistEmpty) onBack()
    }

    var activeTokenId by remember(resetTokenFlag) {
        mutableStateOf(Pair(-1, -1))
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            Column {
                PlayerItemInfoBoard(
                    playerState = playerState,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                PlayerControllerBoard(
                    playerState = playerState,
                    onNavigateToPlaylist = onNavigateToPlaylist,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                        .navigationBarsPadding()
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            PlayerMediaBoard(playerState = playerState, onBack = onBack)
            PlayerSubtitleBoard(
                playerState = playerState,
                activeSubtitleIndex = activeTokenId.first,
                activeTokenStartIndex = activeTokenId.second,
                onClickTokenInSubtitleContext = { token, subtitleContext ->
                    onNavigateToPlayerWord(
                        token,
                        subtitleContext.subtitle,
                        subtitleContext.language,
                        subtitleContext.mediaId
                    )
                    activeTokenId = Pair(subtitleContext.index, token.startIndex)
                },
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}