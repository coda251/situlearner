package com.coda.situlearner

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.coda.situlearner.feature.restore.navigation.RestoreScreen

class RestoreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            RestoreScreen(
                onNavigateToMainScreen = {
                    gotoMainActivity()
                }
            )
        }
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}