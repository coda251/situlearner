package com.coda.situlearner.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.coda.situlearner.core.database.entity.WordContextEntity
import com.coda.situlearner.core.database.entity.WordEntity
import com.coda.situlearner.core.database.entity.WordQuizInfoEntity
import com.coda.situlearner.core.database.entity.WordWithContextsEntity
import com.coda.situlearner.core.database.model.Language
import com.coda.situlearner.core.database.model.WordProficiency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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
        getWordEntity(wordEntity.word, wordEntity.language).firstOrNull()?.let {
            insertWordContextEntity(wordContextEntity.copy(wordId = it.id))
        } ?: run {
            insertWordEntity(wordEntity)
            insertWordContextEntity(wordContextEntity)
        }
    }

    @Query("UPDATE WordEntity SET proficiency = :proficiency WHERE id = :id")
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
    fun getWordEntity(word: String, language: Language): Flow<WordEntity?>

    @Query("DELETE FROM WordContextEntity WHERE id = :id")
    suspend fun deleteWordContextEntity(id: String)

    @Query("UPDATE WordEntity SET lastViewedDate = :lastViewedDate WHERE id = :id")
    suspend fun updateWordEntity(id: String, lastViewedDate: Instant)

    @Transaction
    @Query(
        """
        SELECT w.* FROM WordEntity w
        LEFT JOIN WordQuizInfoEntity q ON w.id = q.wordId
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

    @Query("SELECT * FROM WordQuizInfoEntity WHERE wordId IN (:ids)")
    suspend fun getWordQuizInfoEntities(ids: Set<String>): List<WordQuizInfoEntity>

    @Upsert
    suspend fun upsertWordQuizInfoEntities(infoList: List<WordQuizInfoEntity>)

    @Transaction
    suspend fun updateWordEntities(idToProficiency: Map<String, WordProficiency>) {
        idToProficiency.entries.forEach {
            updateWordEntity(id = it.key, proficiency = it.value)
        }
    }
}