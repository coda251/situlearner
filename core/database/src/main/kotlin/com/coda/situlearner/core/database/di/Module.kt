package com.coda.situlearner.core.database.di

import android.content.Context
import androidx.room.Room
import com.coda.situlearner.core.cfg.AppConfig.ROOM_DATABASE_FILENAME
import com.coda.situlearner.core.database.SituDatabase
import com.coda.situlearner.core.database.helper.DatabaseHelper
import org.koin.dsl.module

val databaseModule = module {
    single {
        provideDatabase(get())
    }

    single { get<SituDatabase>().mediaLibraryDao() }
    single { get<SituDatabase>().wordBankDao() }
    single { DatabaseHelper(get()) }
}

private fun provideDatabase(context: Context) = Room.databaseBuilder(
    context,
    SituDatabase::class.java,
    ROOM_DATABASE_FILENAME
).addMigrations(SituDatabase.From4to5Migration()).build()