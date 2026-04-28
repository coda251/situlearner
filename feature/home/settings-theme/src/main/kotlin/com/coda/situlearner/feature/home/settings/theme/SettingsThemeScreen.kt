package com.coda.situlearner.feature.home.settings.theme

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.SingleChoiceSelector
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SettingsThemeScreen(
    onBack: () -> Unit,
    viewModel: SettingsThemeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsThemeScreen(
        uiState = uiState,
        onBack = onBack,
        onSelectDarkThemeMode = viewModel::setDarkThemeMode,
        onSelectThemeColorMode = viewModel::setThemeColorMode,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsThemeScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSelectDarkThemeMode: (DarkThemeMode) -> Unit,
    onSelectThemeColorMode: (ThemeColorMode) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(onBack)
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                UiState.Loading -> {}
                is UiState.Success -> {
                    ContentBoard(
                        darkThemeMode = uiState.darkThemeMode,
                        themeColorMode = uiState.themeColorMode,
                        onSelectDarkThemeMode = onSelectDarkThemeMode,
                        onSelectThemeColorMode = onSelectThemeColorMode,
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentBoard(
    darkThemeMode: DarkThemeMode,
    themeColorMode: ThemeColorMode,
    onSelectDarkThemeMode: (DarkThemeMode) -> Unit,
    onSelectThemeColorMode: (ThemeColorMode) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ThemeColorModeSelector(themeColorMode, onSelectThemeColorMode)
        DarkThemeModeSelector(darkThemeMode, onSelectDarkThemeMode)
    }
}

@Composable
private fun ThemeColorModeSelector(
    themeColorMode: ThemeColorMode,
    onSelect: (ThemeColorMode) -> Unit
) {
    SingleChoiceSelector(
        currentValue = themeColorMode,
        choices = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ThemeColorMode.entries
        else ThemeColorMode.entries.filter { it != ThemeColorMode.DynamicWithWallpaper },
        headline = stringResource(R.string.home_settings_theme_screen_theme_color_mode),
        supportingText = themeColorMode.asText(),
        valueToText = { it.asText() },
        onConfirm = onSelect
    )
}

@Composable
private fun DarkThemeModeSelector(
    darkThemeMode: DarkThemeMode,
    onSelect: (DarkThemeMode) -> Unit
) {
    SingleChoiceSelector(
        currentValue = darkThemeMode,
        choices = DarkThemeMode.entries,
        headline = stringResource(R.string.home_settings_theme_screen_dark_theme_mode),
        supportingText = darkThemeMode.asText(),
        valueToText = { it.asText() },
        onConfirm = onSelect
    )
}

@Composable
private fun DarkThemeMode.asText() = when (this) {
    DarkThemeMode.FollowSystem -> stringResource(R.string.home_settings_theme_screen_dark_theme_mode_follow_system)
    DarkThemeMode.Light -> stringResource(R.string.home_settings_theme_screen_dark_theme_mode_light)
    DarkThemeMode.Dark -> stringResource(R.string.home_settings_theme_screen_dark_theme_mode_dark)
}

@Composable
private fun ThemeColorMode.asText() = when (this) {
    ThemeColorMode.Static -> stringResource(R.string.home_settings_theme_screen_theme_color_static)
    ThemeColorMode.DynamicWithThumbnail -> stringResource(R.string.home_settings_theme_screen_theme_color_dynamic_with_thumbnail)
    ThemeColorMode.DynamicWithWallpaper -> stringResource(R.string.home_settings_theme_screen_theme_color_dynamic_with_wallpaper)
}