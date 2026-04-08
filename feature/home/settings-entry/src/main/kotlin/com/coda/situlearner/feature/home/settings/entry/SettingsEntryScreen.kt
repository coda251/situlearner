package com.coda.situlearner.feature.home.settings.entry

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.feature.home.settings.entry.model.ExportState
import com.coda.situlearner.feature.home.settings.entry.model.VersionState
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun SettingsEntryScreen(
    onNavigateToChatbot: () -> Unit,
    onNavigateToWord: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToTheme: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsEntryViewModel = koinViewModel()
) {
    val versionState by viewModel.versionState.collectAsStateWithLifecycle()
    val exportState by viewModel.exportState.collectAsStateWithLifecycle()

    SettingsEntryScreen(
        versionState = versionState,
        exportState = exportState,
        onClickTheme = onNavigateToTheme,
        onClickPlayer = onNavigateToPlayer,
        onClickWord = onNavigateToWord,
        onClickChatbot = onNavigateToChatbot,
        onCheckUpdate = viewModel::checkAppUpdate,
        onExport = viewModel::exportData,
        onResetExportState = viewModel::resetExportState,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsEntryScreen(
    versionState: VersionState,
    exportState: ExportState,
    onClickTheme: () -> Unit,
    onClickPlayer: () -> Unit,
    onClickWord: () -> Unit,
    onClickChatbot: () -> Unit,
    onCheckUpdate: (String?) -> Unit,
    onExport: (Uri) -> Unit,
    onResetExportState: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.home_settings_entry_screen_title))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            SettingsContentBoard(
                versionState = versionState,
                exportState = exportState,
                snackbarHostState = snackbarHostState,
                onClickTheme = onClickTheme,
                onClickWord = onClickWord,
                onClickChatbot = onClickChatbot,
                onClickPlayer = onClickPlayer,
                onCheckUpdate = onCheckUpdate,
                onExport = onExport,
                onResetExportState = onResetExportState
            )
        }
    }
}

@Composable
private fun SettingsContentBoard(
    versionState: VersionState,
    exportState: ExportState,
    snackbarHostState: SnackbarHostState,
    onClickTheme: () -> Unit,
    onClickPlayer: () -> Unit,
    onClickWord: () -> Unit,
    onClickChatbot: () -> Unit,
    onCheckUpdate: (String?) -> Unit,
    onExport: (Uri) -> Unit,
    onResetExportState: () -> Unit,
) {
    Column {
        ThemeConfigItem(onClickTheme)
        PlayerConfigItem(onClickPlayer)
        WordConfigItem(onClickWord)
        ChatbotConfigItem(onClickChatbot)
        ExportDataItem(exportState, snackbarHostState, onExport, onResetExportState)
        AppVersionCheckItem(versionState, onCheckUpdate)
    }
}

@Composable
private fun ThemeConfigItem(
    onClickTheme: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_entry_screen_theme)
            )
        },
        supportingContent = {
            Text(text = stringResource(R.string.home_settings_entry_screen_theme_desc))
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.colors_24dp_000000_fill0_wght400_grad0_opsz24),
                contentDescription = null
            )
        },
        modifier = Modifier.clickable(onClick = onClickTheme)
    )
}

@Composable
private fun PlayerConfigItem(
    onClickPlayer: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_entry_screen_player)
            )
        },
        supportingContent = {
            Text(text = stringResource(R.string.home_settings_entry_screen_player_desc))
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.music_video_24dp_000000_fill0_wght400_grad0_opsz24),
                contentDescription = null
            )
        },
        modifier = Modifier.clickable(onClick = onClickPlayer)
    )
}

@Composable
private fun WordConfigItem(
    onClickWord: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_entry_screen_word)
            )
        },
        supportingContent = {
            Text(text = stringResource(R.string.home_settings_entry_screen_word_desc))
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.dictionary_24dp_000000_fill0_wght400_grad0_opsz24),
                contentDescription = null
            )
        },
        modifier = Modifier.clickable(onClick = onClickWord)
    )
}

@Composable
private fun ChatbotConfigItem(
    onClickChatbot: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = stringResource(R.string.home_settings_entry_screen_chatbot))
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.home_settings_entry_screen_chatbot_desc)
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.smart_toy_24dp_000000_fill0_wght400_grad0_opsz24),
                contentDescription = null
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
            Text(text = stringResource(R.string.home_settings_entry_screen_current_version))
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
                            VersionState.Failed -> R.string.home_settings_entry_screen_check_failed
                            VersionState.Loading -> R.string.home_settings_entry_screen_checking
                            VersionState.NotChecked -> R.string.home_settings_entry_screen_check_update
                            VersionState.UpToDate -> R.string.home_settings_entry_screen_up_to_date
                            is VersionState.UpdateAvailable -> R.string.home_settings_entry_screen_update_available
                        }
                    ) + when (versionState) {
                        is VersionState.UpdateAvailable -> " ${versionState.version}"
                        else -> ""
                    }
                )
            }
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.update_24dp_000000_fill0_wght400_grad0_opsz24),
                contentDescription = null
            )
        },
        modifier = Modifier.clickable { handleClick() }
    )
}

@Composable
private fun ExportDataItem(
    exportState: ExportState,
    snackbarHostState: SnackbarHostState,
    onExport: (Uri) -> Unit,
    onResetExportState: () -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { it?.let { onExport(it) } }

    val msgSuccess = stringResource(R.string.home_settings_entry_screen_export_success)
    val msgError = stringResource(R.string.home_settings_entry_screen_export_error)

    LaunchedEffect(exportState) {
        when (exportState) {
            is ExportState.Success -> {
                snackbarHostState.showSnackbar(
                    message = msgSuccess,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
                onResetExportState()
            }

            is ExportState.Error -> {
                snackbarHostState.showSnackbar(
                    message = "${msgError}${exportState.message}",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
                onResetExportState()
            }

            else -> {}
        }
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_entry_screen_export_data)
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.home_settings_entry_screen_export_data_desc)
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.export_notes_24dp_000000_fill0_wght400_grad0_opsz24),
                contentDescription = null
            )
        },
        modifier = Modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        launcher.launch(null)
                        showDialog = false
                    }
                ) {
                    Text(text = stringResource(coreR.string.core_ui_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(text = stringResource(coreR.string.core_ui_cancel))
                }
            },
            title = {
                Text(text = stringResource(R.string.home_settings_entry_screen_export_dialog_title))
            },
            text = {
                Text(text = stringResource(R.string.home_settings_entry_screen_export_dialog_text))
            },
        )
    }

    if (exportState is ExportState.Running) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            text = {
                Text(text = stringResource(R.string.home_settings_entry_screen_exporting_data))
            }
        )
    }
}