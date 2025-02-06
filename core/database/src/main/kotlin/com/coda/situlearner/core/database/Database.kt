package com.coda.situlearner.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coda.situlearner.core.database.dao.MediaLibraryDao
import com.coda.situlearner.core.database.dao.WordBankDao
import com.coda.situlearner.core.database.entity.MediaCollectionEntity
import com.coda.situlearner.core.database.entity.MediaFileEntity
import com.coda.situlearner.core.database.entity.WordContextEntity
import com.coda.situlearner.core.database.entity.WordContextEntityView
import com.coda.situlearner.core.database.entity.WordEntity
import com.coda.situlearner.core.database.util.InstantConverter
import com.coda.situlearner.core.database.util.MeaningsConverter

@Database(
    version = 1,
    exportSchema = true,
    entities = [
        MediaFileEntity::class,
        MediaCollectionEntity::class,
        WordEntity::class,
        WordContextEntity::class,
    ],
    views = [
        WordContextEntityView::class,
    ]
)
@TypeConverters(
    InstantConverter::class,
    MeaningsConverter::class,
)
abstract class SituDatabase : RoomDatabase() {
    abstract fun mediaLibraryDao(): MediaLibraryDao
    abstract fun wordBankDao(): WordBankDao
}