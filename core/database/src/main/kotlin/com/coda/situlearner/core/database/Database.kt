package com.coda.situlearner.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.coda.situlearner.core.database.dao.MediaLibraryDao
import com.coda.situlearner.core.database.dao.WordBankDao
import com.coda.situlearner.core.database.entity.MediaCollectionEntity
import com.coda.situlearner.core.database.entity.MediaFileEntity
import com.coda.situlearner.core.database.entity.WordContextEntity
import com.coda.situlearner.core.database.entity.WordContextEntityView
import com.coda.situlearner.core.database.entity.WordEntity
import com.coda.situlearner.core.database.entity.WordQuizInfoEntity
import com.coda.situlearner.core.database.util.InstantConverter
import com.coda.situlearner.core.database.util.MeaningsConverter

@Database(
    version = 3,
    exportSchema = true,
    entities = [
        MediaFileEntity::class,
        MediaCollectionEntity::class,
        WordEntity::class,
        WordContextEntity::class,
        WordQuizInfoEntity::class,
    ],
    views = [
        WordContextEntityView::class,
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = SituDatabase.From2to3Migration::class)
    ]
)
@TypeConverters(
    InstantConverter::class,
    MeaningsConverter::class,
)
abstract class SituDatabase : RoomDatabase() {
    abstract fun mediaLibraryDao(): MediaLibraryDao
    abstract fun wordBankDao(): WordBankDao

    @DeleteColumn(
        tableName = "WordContextEntity",
        columnName = "partOfSpeech"
    )
    class From2to3Migration : AutoMigrationSpec
}