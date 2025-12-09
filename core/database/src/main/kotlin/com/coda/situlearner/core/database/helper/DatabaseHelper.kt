package com.coda.situlearner.core.database.helper

import com.coda.situlearner.core.database.SituDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseHelper(private val database: SituDatabase) {
    suspend fun prepareForBackup() {
        withContext(Dispatchers.IO) {
            database.openHelper.writableDatabase.run {
                // update .db to the latest one from .db-wal
                query("PRAGMA wal_checkpoint(FULL)").use {}
            }
        }
    }
}