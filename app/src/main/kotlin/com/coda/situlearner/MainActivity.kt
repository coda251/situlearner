package com.coda.situlearner

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.ModalBottomSheetLayout
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.ui.theme.SituLearnerTheme
import com.coda.situlearner.infra.player.PlayerStateProvider
import com.coda.situlearner.navigation.AppNavHost
import com.coda.situlearner.service.PlayerService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by inject()

    private val mediaServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            PlayerStateProvider.register((service as? PlayerService.PlayerBinder)?.state)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            PlayerStateProvider.unregister()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // it's a workaround to read specific subtitle file formats (.ass or self-defined .stk),
        // see https://stackoverflow.com/questions/69472550
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        }

        bindService(
            Intent(this, PlayerService::class.java),
            mediaServiceConnection,
            Context.BIND_AUTO_CREATE
        )

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            navController.navigatorProvider.addNavigator(bottomSheetNavigator)

            val useDarkTheme = shouldUseDarkTheme(uiState)

            // referred to https://github.com/android/nowinandroid
            DisposableEffect(useDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { useDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        DefaultLightScrim,
                        DefaultDarkScrim,
                    ) { useDarkTheme },
                )
                onDispose {}
            }

            SituLearnerTheme(
                useDarkTheme = useDarkTheme,
                colorMode = getThemeColorMode(uiState),
                themeColor = getThemeColor(uiState)
            ) {
                ModalBottomSheetLayout(
                    bottomSheetNavigator = bottomSheetNavigator,
                    scrimColor = androidx.compose.ui.graphics.Color.Unspecified
                ) {
                    AppNavHost(
                        appNavController = navController,
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        unbindService(mediaServiceConnection)
        PlayerStateProvider.unregister()

        super.onDestroy()
    }
}

@Composable
private fun shouldUseDarkTheme(uiState: MainActivityUiState) = when (uiState) {
    MainActivityUiState.Loading -> isSystemInDarkTheme()
    is MainActivityUiState.Success -> when (uiState.darkThemeMode) {
        DarkThemeMode.Light -> false
        DarkThemeMode.Dark -> true
        DarkThemeMode.FollowSystem -> isSystemInDarkTheme()
    }
}

@Composable
private fun getThemeColorMode(uiState: MainActivityUiState) = when (uiState) {
    MainActivityUiState.Loading -> ThemeColorMode.Static
    is MainActivityUiState.Success -> uiState.themeColorMode
}

@Composable
private fun getThemeColor(uiState: MainActivityUiState) = when (uiState) {
    MainActivityUiState.Loading -> androidx.compose.ui.graphics.Color(AppConfig.DEFAULT_THEME_COLOR)
    is MainActivityUiState.Success -> uiState.themeColor
}

private val DefaultLightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

private val DefaultDarkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)