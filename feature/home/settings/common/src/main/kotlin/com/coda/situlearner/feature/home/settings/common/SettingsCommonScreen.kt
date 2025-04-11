package com.coda.situlearner.feature.home.settings.common

import android.os.Build
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
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.ui.util.asText
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
        onSelectWordLibraryLanguage = viewModel::setWordLibraryLanguage,
        onSetQuizWordCount = viewModel::setQuizWordCount,
        onSetRecommendedWordCount = viewModel::setRecommendedWordCount,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsCommonScreen(
    uiState: SettingsCommonUiState,
    onSelectDarkThemeMode: (DarkThemeMode) -> Unit,
    onSelectThemeColorMode: (ThemeColorMode) -> Unit,
    onSelectWordLibraryLanguage: (Language) -> Unit,
    onSetQuizWordCount: (UInt) -> Unit,
    onSetRecommendedWordCount: (UInt) -> Unit,
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
                        wordLibraryLanguage = uiState.wordLibraryLanguage,
                        quizWordCount = uiState.quizWordCount,
                        recommendedWordCount = uiState.recommendedWordCount,
                        onSelectDarkThemeMode = onSelectDarkThemeMode,
                        onSelectThemeColorMode = onSelectThemeColorMode,
                        onSelectWordLibraryLanguage = onSelectWordLibraryLanguage,
                        onSetQuizWordCount = onSetQuizWordCount,
                        onSetRecommendedWordCount = onSetRecommendedWordCount
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
    wordLibraryLanguage: Language,
    quizWordCount: UInt,
    recommendedWordCount: UInt,
    onSelectDarkThemeMode: (DarkThemeMode) -> Unit,
    onSelectThemeColorMode: (ThemeColorMode) -> Unit,
    onSelectWordLibraryLanguage: (Language) -> Unit,
    onSetQuizWordCount: (UInt) -> Unit,
    onSetRecommendedWordCount: (UInt) -> Unit,
) {
    Column {
        ThemeColorModeSelector(themeColorMode, onSelectThemeColorMode)
        DarkThemeModeSelector(darkThemeMode, onSelectDarkThemeMode)
        WordFilterLanguageSelector(wordLibraryLanguage, onSelectWordLibraryLanguage)
        QuizWordCountSelector(quizWordCount, onSetQuizWordCount)
        RecommendedWordCountSelector(recommendedWordCount, onSetRecommendedWordCount)
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
                        if (it != ThemeColorMode.DynamicWithWallpaper || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
private fun WordFilterLanguageSelector(
    language: Language,
    onSelectLanguage: (Language) -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_common_screen_word_library_language)
            )
        },
        supportingContent = {
            Text(
                text = language.asText()
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
                    AppConfig.sourceLanguages.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = it == language,
                                onClick = { onSelectLanguage(it) },
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
private fun QuizWordCountSelector(
    quizWordCount: UInt,
    onSetQuizWordCount: (UInt) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_common_screen_quiz_word_count)
            )
        },
        supportingContent = {
            Text(text = quizWordCount.toString())
        },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    if (showDialog) {
        WordCountSelectorDialog(
            initialCount = quizWordCount,
            valueRange = 5f..50f,
            steps = 8,
            onDismiss = {
                showDialog = false
            },
            onConfirm = {
                onSetQuizWordCount(it)
                showDialog = false
            }
        )
    }
}

@Composable
private fun RecommendedWordCountSelector(
    recommendedWordCount: UInt,
    onSetRecommendedWordCount: (UInt) -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_common_screen_recommended_word_count)
            )
        },
        supportingContent = {
            Text(text = recommendedWordCount.toString())
        },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    if (showDialog) {
        WordCountSelectorDialog(
            initialCount = recommendedWordCount,
            valueRange = 10f..50f,
            steps = 3,
            onDismiss = {
                showDialog = false
            },
            onConfirm = {
                onSetRecommendedWordCount(it)
                showDialog = false
            }
        )
    }
}

@Composable
private fun WordCountSelectorDialog(
    initialCount: UInt,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onDismiss: () -> Unit,
    onConfirm: (UInt) -> Unit,
) {
    var count by remember {
        mutableStateOf(initialCount)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(count) }
            ) {
                Text(text = stringResource(R.string.home_settings_common_screen_dialog_ok))
            }
        },
        text = {
            Column {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.home_settings_common_screen_count)) },
                    trailingContent = { Text("$count") },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                Slider(
                    modifier = Modifier.padding(12.dp),
                    value = count.toFloat(),
                    onValueChange = { count = it.toUInt() },
                    valueRange = valueRange,
                    steps = steps
                )
            }
        }
    )
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