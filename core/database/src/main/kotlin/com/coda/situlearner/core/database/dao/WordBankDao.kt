package com.coda.situlearner.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.coda.situlearner.core.database.entity.MeaningQuizStatsEntity
import com.coda.situlearner.core.database.entity.TranslationQuizStatsEntity
import com.coda.situlearner.core.database.entity.WordContextEntity
import com.coda.situlearner.core.database.entity.WordEntity
import com.coda.situlearner.core.database.entity.WordWithContextsEntity
import com.coda.situlearner.core.database.model.Language
import com.coda.situlearner.core.database.model.WordProficiency
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface WordBankDao {

    @Transaction
    @Query("SELECT * FROM WordEntity WHERE language = :language")
    fun getWordWithContextsEntities(language: Language): Flow<List<WordWithContextsEntity>>

    @Insert
    suspend fun insertWordContextEntity(wordContextEntity: WordContextEntity)

    @Insert
    suspend fun insertWordEntity(wordEntity: WordEntity)

    @Transaction
    suspend fun insertWordEntityAndWordContextEntity(
        wordEntity: WordEntity,
        wordContextEntity: WordContextEntity
    ) {
        getWordEntity(wordEntity.word, wordEntity.language)?.let {
            insertWordContextEntity(wordContextEntity.copy(wordId = it.id))
        } ?: run {
            insertWordEntity(wordEntity)
            insertWordContextEntity(wordContextEntity)
        }
    }

    @Query("UPDATE WordEntity SET meaningProficiency = :proficiency WHERE id = :id")
    suspend fun updateWordEntity(id: String, proficiency: WordProficiency)

    @Query(
        """
        SELECT * FROM WordContextEntity
        WHERE mediaId = :mediaId
          AND subtitleStartTimeInMs = :subtitleStartTimeInMs
          AND subtitleSourceText = :subtitleSourceText
          AND wordStartIndex = :wordStartIndex
          AND wordEndIndex = :wordEndIndex
        """
    )
    fun getWordContextEntity(
        mediaId: String,
        subtitleStartTimeInMs: Long,
        subtitleSourceText: String,
        wordStartIndex: Int,
        wordEndIndex: Int,
    ): Flow<WordContextEntity?>

    @Transaction
    @Query("SELECT * FROM WordEntity WHERE id = :id")
    fun getWordWithContextEntity(id: String): Flow<WordWithContextsEntity?>

    @Query(
        "SELECT * FROM WordEntity WHERE word = :word AND language = :language"
    )
    suspend fun getWordEntity(word: String, language: Language): WordEntity?

    @Query("DELETE FROM WordContextEntity WHERE id = :id")
    suspend fun deleteWordContextEntity(id: String)

    @Query("UPDATE WordEntity SET lastViewedDate = :lastViewedDate WHERE id = :id")
    suspend fun updateWordEntity(id: String, lastViewedDate: Instant)

    @Transaction
    @Query(
        """
        SELECT w.* FROM WordEntity w
        LEFT JOIN MeaningQuizStatsEntity q ON w.id = q.wordId
        WHERE 
            w.language == :language
            AND (q.wordId IS NULL OR (q.nextQuizDate <= :currentDate))
        ORDER BY
            CASE WHEN q.wordId IS NULL THEN 0 ELSE 1 END,
            q.intervalDays ASC
        LIMIT :number
        """
    )
    suspend fun getWordWithContextEntities(
        language: Language,
        currentDate: Instant,
        number: Int
    ): List<WordWithContextsEntity>

    @Query("SELECT * FROM TranslationQuizStatsEntity WHERE wordId = :wordId")
    suspend fun getTranslationQuizStatsEntity(wordId: String): TranslationQuizStatsEntity?

    @Transaction
    @Query(
        """
        SELECT w.* FROM WordEntity w
        LEFT JOIN TranslationQuizStatsEntity t ON w.id = t.wordId
        WHERE 
            w.language == :language AND w.meaningProficiency = :proficiency
            AND (t.wordId IS NULL OR t.nextQuizDate <= :currentDate)
        ORDER BY
            CASE WHEN t.wordId IS NULL THEN 0 ELSE 1 END,
            t.nextQuizDate ASC,
            w.createdDate ASC
        LIMIT 1
        """
    )
    suspend fun getWordEntity(
        language: Language,
        currentDate: Instant,
        proficiency: WordProficiency
    ): WordEntity?

    @Query("SELECT * FROM MeaningQuizStatsEntity WHERE wordId IN (:ids)")
    suspend fun getMeaningQuizStatsEntities(ids: Set<String>): List<MeaningQuizStatsEntity>

    @Upsert
    suspend fun upsertMeaningQuizStatsEntity(entities: List<MeaningQuizStatsEntity>)

    @Transaction
    suspend fun updateWordEntities(idToProficiency: Map<String, WordProficiency>) {
        idToProficiency.entries.forEach {
            updateWordEntity(id = it.key, proficiency = it.value)
        }
    }

    @Query("SELECT * FROM WordEntity WHERE id = :id")
    suspend fun getWordEntity(id: String): WordEntity?

    @Query("DELETE FROM WordEntity WHERE id = :id")
    suspend fun deleteWordEntity(id: String)

    @Query("UPDATE WordContextEntity SET wordId = :newWordId WHERE wordId = :oldWordId")
    suspend fun updateWordContextEntity(oldWordId: String, newWordId: String)

    @Update
    suspend fun updateWordEntityInternal(wordEntity: WordEntity)

    @Transaction
    suspend fun updateWordEntity(wordEntity: WordEntity) {
        getWordEntity(wordEntity.word, wordEntity.language)?.let {
            // find duplicate
            if (it.id != wordEntity.id) {
                updateWordContextEntity(it.id, wordEntity.id)
                deleteWordEntity(it.id)
            }
        }

        updateWordEntityInternal(wordEntity)
    }

    @Upsert
    suspend fun upsertTranslationQuizStatsEntity(entity: TranslationQuizStatsEntity)

    @Query("SELECT * FROM MeaningQuizStatsEntity ORDER BY nextQuizDate ASC LIMIT 1")
    suspend fun getLatestMeaningQuizStatsEntity(): MeaningQuizStatsEntity?
}