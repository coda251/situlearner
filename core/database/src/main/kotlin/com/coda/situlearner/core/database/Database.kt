package com.coda.situlearner.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.coda.situlearner.core.database.dao.MediaLibraryDao
import com.coda.situlearner.core.database.dao.WordBankDao
import com.coda.situlearner.core.database.entity.MeaningQuizStatsEntity
import com.coda.situlearner.core.database.entity.MediaCollectionEntity
import com.coda.situlearner.core.database.entity.MediaFileEntity
import com.coda.situlearner.core.database.entity.TranslationQuizStatsEntity
import com.coda.situlearner.core.database.entity.WordContextEntity
import com.coda.situlearner.core.database.entity.WordContextEntityView
import com.coda.situlearner.core.database.entity.WordEntity
import com.coda.situlearner.core.database.util.InstantConverter
import com.coda.situlearner.core.database.util.MeaningsConverter

@Database(
    version = 5,
    exportSchema = true,
    entities = [
        MediaFileEntity::class,
        MediaCollectionEntity::class,
        WordEntity::class,
        WordContextEntity::class,
        MeaningQuizStatsEntity::class,
        TranslationQuizStatsEntity::class
    ],
    views = [
        WordContextEntityView::class,
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = SituDatabase.From2to3Migration::class),
        AutoMigration(from = 3, to = 4, spec = SituDatabase.From3to4Migration::class),
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

    @RenameTable(
        fromTableName = "WordQuizInfoEntity",
        toTableName = "MeaningQuizStatsEntity"
    )
    @RenameColumn(
        tableName = "WordEntity",
        fromColumnName = "proficiency",
        toColumnName = "meaningProficiency"
    )
    class From3to4Migration : AutoMigrationSpec

    class From4to5Migration : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE WordEntity ADD COLUMN createdDate INTEGER NOT NULL DEFAULT 0")
            db.execSQL(
                """
            UPDATE WordEntity
            SET createdDate = COALESCE(lastViewedDate,
                                       CAST(strftime('%s','now') AS INTEGER) * 1000)
            WHERE createdDate = 0
              AND NOT EXISTS (
                  SELECT 1 FROM WordContextEntity
                  WHERE WordContextEntity.wordId = WordEntity.id
              )
            """.trimIndent()
            )
            db.execSQL(
                """
            UPDATE WordEntity
            SET createdDate = (
                SELECT MIN(createdDate)
                FROM WordContextEntity
                WHERE WordContextEntity.wordId = WordEntity.id
            )
            WHERE EXISTS (
                SELECT 1 FROM WordContextEntity
                WHERE WordContextEntity.wordId = WordEntity.id
            )
            """.trimIndent()
            )
        }
    }
}