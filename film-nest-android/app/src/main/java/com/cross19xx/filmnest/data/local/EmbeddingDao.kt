package com.cross19xx.filmnest.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface EmbeddingDao {

    @Upsert
    suspend fun upsert(entity: MediaEmbeddingEntity)

    @Query("SELECT * FROM media_embeddings")
    suspend fun getAll(): List<MediaEmbeddingEntity>

    @Query("SELECT * FROM media_embeddings WHERE mediaId = :id")
    suspend fun getById(id: Int): MediaEmbeddingEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM media_embeddings WHERE mediaId = :id)")
    suspend fun existsById(id: Int): Boolean

    @Query("SELECT COUNT(*) FROM media_embeddings")
    suspend fun count(): Int

    @Query("DELETE FROM media_embeddings WHERE mediaId = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM media_embeddings")
    suspend fun deleteAll()
}
