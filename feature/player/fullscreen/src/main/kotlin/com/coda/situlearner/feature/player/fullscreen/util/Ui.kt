package com.coda.situlearner.feature.player.fullscreen.util

import android.content.pm.ActivityInfo
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
internal fun ChangeScreenOrientation(isExiting: Boolean) {
    val activity = LocalActivity.current as ComponentActivity
    DisposableEffect(isExiting) {
        if (!isExiting) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        onDispose {
            if (!isExiting) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
}

@Composable
internal fun SetupWindowForFullScreenVideo(isExiting: Boolean) {
    val activity = LocalActivity.current as ComponentActivity
    val view = LocalView.current

    DisposableEffect(isExiting) {
        val window = activity.window
        val insetsController = WindowCompat.getInsetsController(window, view)

        if (!isExiting) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }

        onDispose {
            // unexpected dispose
            if (!isExiting) {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
                WindowCompat.setDecorFitsSystemWindows(window, true)
            }
        }
    }
}

@Composable
internal fun KeepScreenOn(view: View) {
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = false
        }
    }
}