package com.coda.situlearner.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.coda.situlearner.core.database.entity.MediaCollectionEntity
import com.coda.situlearner.core.database.entity.MediaCollectionWithFilesEntity
import com.coda.situlearner.core.database.entity.MediaFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaLibraryDao {

    @Query("SELECT * FROM MediaCollectionEntity")
    fun getMediaCollectionEntities(): Flow<List<MediaCollectionEntity>>

    @Transaction
    @Query("SELECT * FROM MediaCollectionEntity WHERE id = :id")
    fun getMediaCollectionWithFilesEntityById(id: String): Flow<MediaCollectionWithFilesEntity?>

    @Transaction
    @Query("SELECT * FROM MediaCollectionEntity WHERE url = :url")
    fun getMediaCollectionWithFilesEntityByUrl(url: String): Flow<MediaCollectionWithFilesEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMediaCollectionEntity(mediaCollectionEntity: MediaCollectionEntity)

    @Insert
    suspend fun insertMediaFileEntities(mediaFileEntities: List<MediaFileEntity>)

    @Transaction
    suspend fun insertMediaCollectionWithFilesEntity(mediaCollectionWithFilesEntity: MediaCollectionWithFilesEntity) {
        insertMediaCollectionEntity(mediaCollectionWithFilesEntity.collection)
        insertMediaFileEntities(mediaCollectionWithFilesEntity.files)
    }

    @Query("UPDATE MediaCollectionEntity SET name = :name WHERE id = :id")
    suspend fun updateMediaCollectionEntityName(id: String, name: String)

    @Query("DELETE FROM MediaCollectionEntity WHERE id = :id")
    suspend fun deleteMediaCollectionEntity(id: String)

    @Query("UPDATE MediaFileEntity SET durationInMs = :durationInMs WHERE id = :id")
    suspend fun updateMediaFileEntity(id: String, durationInMs: Long)

    @Transaction
    suspend fun updateMediaFileEntities(idToDuration: Map<String, Long>) {
        idToDuration.entries.forEach {
            updateMediaFileEntity(it.key, it.value)
        }
    }

    @Query("UPDATE MediaCollectionEntity SET coverUrl = :coverUrl WHERE id = :id")
    suspend fun updateMediaCollectionEntityCoverUrl(id: String, coverUrl: String?)
}