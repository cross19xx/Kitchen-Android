package com.cross19xx.filmnest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MediaEmbeddingEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FilmNestDatabase : RoomDatabase() {
    abstract fun embeddingDao(): EmbeddingDao
}
