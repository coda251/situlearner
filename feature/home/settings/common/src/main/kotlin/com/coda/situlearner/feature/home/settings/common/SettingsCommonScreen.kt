package com.coda.situlearner.feature.home.settings.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.ThemeColorMode
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SettingsCommonScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsCommonViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsCommonScreen(
        uiState = uiState,
        onSelectDarkThemeMode = viewModel::setDarkThemeMode,
        onSelectThemeColorMode = viewModel::setThemeColorMode,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsCommonScreen(
    uiState: SettingsCommonUiState,
    onSelectDarkThemeMode: (DarkThemeMode) -> Unit,
    onSelectThemeColorMode: (ThemeColorMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.home_settings_common_screen_title))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            when (uiState) {
                SettingsCommonUiState.Loading -> {}
                is SettingsCommonUiState.Success -> {
                    SettingsContentBoard(
                        darkThemeMode = uiState.darkThemeMode,
                        themeColorMode = uiState.themeColorMode,
                        onSelectDarkThemeMode = onSelectDarkThemeMode,
                        onSelectThemeColorMode = onSelectThemeColorMode
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsContentBoard(
    darkThemeMode: DarkThemeMode,
    themeColorMode: ThemeColorMode,
    onSelectDarkThemeMode: (DarkThemeMode) -> Unit,
    onSelectThemeColorMode: (ThemeColorMode) -> Unit
) {
    Column {
        ThemeColorModeSelector(themeColorMode, onSelectThemeColorMode)
        DarkThemeModeSelector(darkThemeMode, onSelectDarkThemeMode)
    }
}

@Composable
private fun ThemeColorModeSelector(
    themeColorMode: ThemeColorMode,
    onSelect: (ThemeColorMode) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_common_screen_theme_color_mode)
            )
        },
        supportingContent = {
            Text(
                text = themeColorMode.asText()
            )
        },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(text = stringResource(R.string.home_settings_common_screen_dialog_ok))
                }
            },
            text = {
                Column {
                    ThemeColorMode.entries.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = it == themeColorMode,
                                onClick = { onSelect(it) },
                            )
                            Text(text = it.asText())
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun DarkThemeModeSelector(
    darkThemeMode: DarkThemeMode,
    onSelect: (DarkThemeMode) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_common_screen_dark_theme_mode)
            )
        },
        supportingContent = {
            Text(
                text = darkThemeMode.asText()
            )
        },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(text = stringResource(R.string.home_settings_common_screen_dialog_ok))
                }
            },
            text = {
                Column {
                    DarkThemeMode.entries.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = it == darkThemeMode,
                                onClick = { onSelect(it) },
                            )
                            Text(text = it.asText())
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun DarkThemeMode.asText() = when (this) {
    DarkThemeMode.FollowSystem -> stringResource(R.string.home_settings_common_screen_dark_theme_mode_follow_system)
    DarkThemeMode.Light -> stringResource(R.string.home_settings_common_screen_dark_theme_mode_light)
    DarkThemeMode.Dark -> stringResource(R.string.home_settings_common_screen_dark_theme_mode_dark)
}

@Composable
private fun ThemeColorMode.asText() = when (this) {
    ThemeColorMode.Static -> stringResource(R.string.home_settings_common_screen_theme_color_static)
    ThemeColorMode.DynamicWithThumbnail -> stringResource(R.string.home_settings_common_screen_theme_color_dynamic_with_thumbnail)
    ThemeColorMode.DynamicWithWallpaper -> stringResource(R.string.home_settings_common_screen_theme_color_dynamic_with_wallpaper)
}