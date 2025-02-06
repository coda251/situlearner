package com.coda.situlearner.core.database.di

import android.content.Context
import androidx.room.Room
import com.coda.situlearner.core.database.SituDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        provideDatabase(get())
    }

    single { get<SituDatabase>().mediaLibraryDao() }
    single { get<SituDatabase>().wordBankDao() }
}

private fun provideDatabase(context: Context) = Room.databaseBuilder(
    context,
    SituDatabase::class.java,
    "SituLearner.db"
).build()