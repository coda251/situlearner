package com.coda.situlearner.feature.restore

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.cfg.AppConfig.DEFAULT_THEME_COLOR
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.ui.theme.SituLearnerTheme
import com.coda.situlearner.feature.restore.model.RestoreState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun RestoreScreen(
    onFinished: () -> Unit,
    viewModel: RestoreViewModel = koinViewModel()
) {
    val uiState by viewModel.restoreState.collectAsStateWithLifecycle()

    SituLearnerTheme(
        useDarkTheme = false,
        colorMode = ThemeColorMode.Static,
        themeColor = Color(DEFAULT_THEME_COLOR)
    ) {
        RestoreScreen(
            restoreState = uiState,
            onRestoreFinished = onFinished,
            onRestoreData = viewModel::restoreData,
            onResetRestoreState = viewModel::resetRestoreState
        )
    }
}

@Composable
private fun RestoreScreen(
    restoreState: RestoreState,
    onRestoreFinished: () -> Unit,
    onRestoreData: (Uri) -> Unit,
    onResetRestoreState: () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        it?.let { onRestoreData(it) }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        ),
                        center = Offset.Unspecified,
                        radius = 1500f
                    )
                )
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { launcher.launch(arrayOf("application/zip")) },
                ) {
                    Text(text = stringResource(R.string.restore_screen_import_backup_data))
                }
                Spacer(modifier = Modifier.height(100.dp))
                TextButton(onClick = onRestoreFinished) {
                    Text(
                        text = stringResource(R.string.restore_screen_start_without_restoring)
                    )
                }
            }
        }
    }

    val msgError = stringResource(R.string.restore_screen_import_error)

    LaunchedEffect(restoreState) {
        when (restoreState) {
            RestoreState.Success -> onRestoreFinished()
            is RestoreState.Error -> {
                snackbarHostState.showSnackbar(
                    message = "${msgError}${restoreState.message}",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
                onResetRestoreState()
            }

            else -> {}
        }
    }

    if (restoreState is RestoreState.Running) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            text = {
                Text(text = stringResource(R.string.restore_screen_importing_data))
            }
        )
    }
}

@Composable
@Preview
private fun RestoreScreenPreview() {
    RestoreScreen(
        restoreState = RestoreState.Idle,
        onRestoreFinished = {},
        onRestoreData = {},
        onResetRestoreState = {}
    )
}