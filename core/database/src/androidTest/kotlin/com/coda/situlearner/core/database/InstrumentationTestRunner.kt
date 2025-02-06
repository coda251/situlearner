package com.coda.situlearner.core.database

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.runner.AndroidJUnitRunner
import com.coda.situlearner.core.database.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class InstrumentationTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(classLoader, TestApplication::class.java.name, context)
    }
}

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TestApplication)
            modules(
                databaseModule,
                module {
                    Room.inMemoryDatabaseBuilder(
                        this@TestApplication, SituDatabase::class.java,
                    ).build()
                }
            )
        }
    }
}