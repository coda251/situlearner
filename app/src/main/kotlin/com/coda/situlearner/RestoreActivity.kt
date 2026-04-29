package com.coda.situlearner

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import com.coda.situlearner.core.cfg.AppConfig.DEFAULT_THEME_COLOR
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.ui.theme.SituLearnerTheme
import com.coda.situlearner.feature.restore.navigation.RestoreScreen

class RestoreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SituLearnerTheme(
                useDarkTheme = false,
                colorMode = ThemeColorMode.Static,
                themeColor = Color(DEFAULT_THEME_COLOR)
            ) {
                RestoreScreen(
                    onNavigateToMainScreen = {
                        gotoMainActivity()
                    }
                )
            }
        }
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}