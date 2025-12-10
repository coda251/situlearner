package com.coda.situlearner

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.coda.situlearner.core.cfg.AppConfig.ROOM_DATABASE_FILENAME

class LauncherActivity : ComponentActivity() {

    val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            routeToNextActivity()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // it's a workaround to read specific subtitle file formats (.ass),
        // see https://stackoverflow.com/questions/69472550
        if (!Environment.isExternalStorageManager()) {
            permissionLauncher.launch(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
        } else {
            routeToNextActivity()
        }
    }

    private fun routeToNextActivity() {
        startActivity(
            Intent(
                this,
                if (isFirstInstall()) RestoreActivity::class.java
                else MainActivity::class.java
            )
        )
        finish()
    }

    private fun isFirstInstall(): Boolean {
        // Presence of user data indicates upgrade and should bypass restore.
        val dbFile = getDatabasePath(ROOM_DATABASE_FILENAME)
        return !dbFile.exists()
    }
}