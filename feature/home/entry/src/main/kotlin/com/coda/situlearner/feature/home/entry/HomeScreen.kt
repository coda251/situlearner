package com.coda.situlearner.feature.home.entry

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.PlayNextButton
import com.coda.situlearner.core.ui.widget.PlayOrPauseButton
import com.coda.situlearner.feature.home.media.entry.navigation.HomeMediaBaseRoute
import com.coda.situlearner.feature.home.media.entry.navigation.navigateToHomeMediaEntry
import com.coda.situlearner.feature.home.settings.entry.navigation.HomeSettingsBaseRoute
import com.coda.situlearner.feature.home.settings.entry.navigation.navigateToHomeSettingsEntry
import com.coda.situlearner.feature.home.word.entry.navigation.HomeWordBaseRoute
import com.coda.situlearner.feature.home.word.entry.navigation.navigateToHomeWordEntry
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import kotlin.reflect.KClass

@Composable
internal fun HomeScreen(
    onNavigateToWordList: (WordListType, String?) -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToWordQuiz: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            Column {
                PlayerBottomBar(
                    playerState = playerState,
                    onNavigateToPlayer = onNavigateToPlayer,
                )
                NavBottomBar(navController = navController)
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            HomeNavHost(
                onNavigateToWordList = onNavigateToWordList,
                onNavigateToWordDetail = onNavigateToWordDetail,
                onNavigateToWordQuiz = onNavigateToWordQuiz,
                navController = navController,
            )
        }
    }
}

@Composable
private fun PlayerBottomBar(
    playerState: PlayerState,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlaying by playerState.isPlaying.collectAsStateWithLifecycle()
    val playlist by playerState.playlist.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = playlist.isNotEmpty(),
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        playlist.currentItem?.let { item ->
            PlayerBottomBar(
                playlistItem = item,
                isPlaying = isPlaying,
                onToggleShouldBePlaying = {
                    if (it) playerState.play() else playerState.pause()
                },
                onPlayNext = playerState::playNext,
                onNavigateToPlayer = onNavigateToPlayer,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun PlayerBottomBar(
    playlistItem: PlaylistItem,
    isPlaying: Boolean,
    onToggleShouldBePlaying: (Boolean) -> Unit,
    onPlayNext: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .clickable(
                onClick = onNavigateToPlayer
            ),
        leadingContent = {
            AsyncMediaImage(
                model = playlistItem.thumbnailUrl, modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        },
        headlineContent = {
            Text(
                text = playlistItem.name,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.basicMarquee(),
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = {
            Row {
                PlayOrPauseButton(isPlaying) {
                    onToggleShouldBePlaying(!isPlaying)
                }

                PlayNextButton(onPlayNext)
            }
        },

        // consistent with nav bottom bar
        colors = ListItemDefaults.colors(containerColor = NavigationBarDefaults.containerColor),
        tonalElevation = NavigationBarDefaults.Elevation
    )
}

@Composable
private fun NavBottomBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        BottomNavRoute.entries.forEach { item ->
            val destination = item.route
            val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(destination) } == true
            val iconId = if (isSelected) item.selectedIcon else item.unSelectedIcon
            NavigationBarItem(
                icon = { Icon(painter = painterResource(iconId), contentDescription = null) },
                label = { Text(stringResource(id = item.title)) },
                selected = isSelected,
                onClick = {
                    val options = navOptions {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    when (item) {
                        BottomNavRoute.Media -> navController.navigateToHomeMediaEntry(options)
                        BottomNavRoute.Word -> navController.navigateToHomeWordEntry(options)
                        BottomNavRoute.Settings -> navController.navigateToHomeSettingsEntry(options)
                    }
                }
            )
        }
    }
}

private enum class BottomNavRoute(
    @StringRes val title: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unSelectedIcon: Int,
    val route: KClass<*>
) {
    Media(
        R.string.home_screen_media,
        R.drawable.video_library_24dp_000000_fill1_wght400_grad0_opsz24,
        R.drawable.video_library_24dp_000000_fill0_wght400_grad0_opsz24,
        HomeMediaBaseRoute::class
    ),

    Word(
        R.string.home_screen_word,
        R.drawable.event_note_24dp_000000_fill1_wght400_grad0_opsz24,
        R.drawable.event_note_24dp_000000_fill0_wght400_grad0_opsz24,
        HomeWordBaseRoute::class
    ),

    Settings(
        R.string.home_screen_settings,
        R.drawable.settings_24dp_000000_fill1_wght400_grad0_opsz24,
        R.drawable.settings_24dp_000000_fill0_wght400_grad0_opsz24,
        HomeSettingsBaseRoute::class
    )
}