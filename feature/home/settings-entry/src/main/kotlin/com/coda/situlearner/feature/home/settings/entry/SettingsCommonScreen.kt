package com.coda.situlearner.feature.home.settings.entry

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.model.data.ChatbotConfig
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.ui.util.asText
import com.coda.situlearner.core.ui.widget.LanguageSelectorDialog
import com.coda.situlearner.core.ui.widget.WordCountSelectorDialog
import com.coda.situlearner.feature.home.settings.entry.model.VersionState
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun SettingsCommonScreen(
    onNavigateToChatbot: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsCommonViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val versionState by viewModel.versionState.collectAsStateWithLifecycle()

    SettingsCommonScreen(
        uiState = uiState,
        versionState = versionState,
        onSelectDarkThemeMode = viewModel::setDarkThemeMode,
        onSelectThemeColorMode = viewModel::setThemeColorMode,
        onSelectWordLibraryLanguage = viewModel::setWordLibraryLanguage,
        onSetRecommendedWordCount = viewModel::setRecommendedWordCount,
        onNavigateToQuiz = onNavigateToQuiz,
        onClickChatbot = onNavigateToChatbot,
        onCheckUpdate = viewModel::checkAppUpdate,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsCommonScreen(
    uiState: SettingsCommonUiState,
    versionState: VersionState,
    onSelectDarkThemeMode: (DarkThemeMode) -> Unit,
    onSelectThemeColorMode: (ThemeColorMode) -> Unit,
    onSelectWordLibraryLanguage: (Language) -> Unit,
    onSetRecommendedWordCount: (UInt) -> Unit,
    onNavigateToQuiz: () -> Unit,
    onClickChatbot: () -> Unit,
    onCheckUpdate: (String?) -> Unit,
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
                        recommendedWordCount = uiState.recommendedWordCount,
                        chatbotConfig = uiState.chatbotConfig,
                        versionState = versionState,
                        onSelectDarkThemeMode = onSelectDarkThemeMode,
                        onSelectThemeColorMode = onSelectThemeColorMode,
                        onSelectWordLibraryLanguage = onSelectWordLibraryLanguage,
                        onSetRecommendedWordCount = onSetRecommendedWordCount,
                        onNavigateToQuiz = onNavigateToQuiz,
                        onClickChatbot = onClickChatbot,
                        onCheckUpdate = onCheckUpdate
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
    recommendedWordCount: UInt,
    chatbotConfig: ChatbotConfig?,
    versionState: VersionState,
    onSelectDarkThemeMode: (DarkThemeMode) -> Unit,
    onSelectThemeColorMode: (ThemeColorMode) -> Unit,
    onSelectWordLibraryLanguage: (Language) -> Unit,
    onSetRecommendedWordCount: (UInt) -> Unit,
    onNavigateToQuiz: () -> Unit,
    onClickChatbot: () -> Unit,
    onCheckUpdate: (String?) -> Unit,
) {
    Column {
        ThemeColorModeSelector(themeColorMode, onSelectThemeColorMode)
        DarkThemeModeSelector(darkThemeMode, onSelectDarkThemeMode)
        WordFilterLanguageSelector(wordLibraryLanguage, onSelectWordLibraryLanguage)
        RecommendedWordCountSelector(recommendedWordCount, onSetRecommendedWordCount)
        QuizSettingsItem(onNavigateToQuiz)
        ChatbotConfigItem(chatbotConfig, onClickChatbot)
        AppVersionCheckItem(versionState, onCheckUpdate)
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
                    Text(text = stringResource(coreR.string.core_ui_ok))
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
                    Text(text = stringResource(coreR.string.core_ui_ok))
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
        LanguageSelectorDialog(
            choices = AppConfig.sourceLanguages,
            currentLanguage = language,
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false },
            onSelect = { onSelectLanguage(it) }
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
private fun QuizSettingsItem(
    onNavigateToQuiz: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_common_screen_word_quiz)
            )
        },
        supportingContent = {
            Text(text = stringResource(R.string.home_settings_common_screen_word_quiz_desc))
        },
        modifier = Modifier.clickable(onClick = onNavigateToQuiz)
    )
}

@Composable
private fun ChatbotConfigItem(
    chatbotConfig: ChatbotConfig?,
    onClickChatbot: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = stringResource(R.string.home_settings_common_screen_chatbot))
        },
        supportingContent = {
            Text(
                text = chatbotConfig?.type?.botName
                    ?: stringResource(R.string.home_settings_common_screen_chatbot_none)
            )
        },
        modifier = Modifier.clickable(onClick = onClickChatbot)
    )
}

@Composable
private fun AppVersionCheckItem(
    versionState: VersionState,
    onCheckUpdate: (String?) -> Unit,
) {
    val context = LocalContext.current

    val currentVersion = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    val handleClick = {
        when (versionState) {
            VersionState.NotChecked, VersionState.Failed -> onCheckUpdate(currentVersion)
            VersionState.Loading, VersionState.UpToDate -> {}
            is VersionState.UpdateAvailable -> {
                val intent = Intent(Intent.ACTION_VIEW, versionState.downloadUrl.toUri())
                context.startActivity(intent)
            }
        }
    }

    ListItem(
        headlineContent = {
            Text(text = stringResource(R.string.home_settings_common_screen_current_version))
        },
        supportingContent = {
            currentVersion?.let { Text(text = it) }
        },
        trailingContent = {
            TextButton(
                onClick = handleClick
            ) {
                Text(
                    text = stringResource(
                        when (versionState) {
                            VersionState.Failed -> R.string.home_settings_common_screen_check_failed
                            VersionState.Loading -> R.string.home_settings_common_screen_checking
                            VersionState.NotChecked -> R.string.home_settings_common_screen_check_update
                            VersionState.UpToDate -> R.string.home_settings_common_screen_up_to_date
                            is VersionState.UpdateAvailable -> R.string.home_settings_common_screen_update_available
                        }
                    ) + when (versionState) {
                        is VersionState.UpdateAvailable -> " ${versionState.version}"
                        else -> ""
                    }
                )
            }
        },
        modifier = Modifier.clickable { handleClick() }
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