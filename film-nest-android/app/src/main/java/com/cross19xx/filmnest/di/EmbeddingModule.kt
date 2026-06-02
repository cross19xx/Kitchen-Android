package com.cross19xx.filmnest.di

import android.content.Context
import androidx.room.Room
import com.cross19xx.filmnest.ai.MediaPipeTextEmbedder
import com.cross19xx.filmnest.ai.TextEmbedder
import com.cross19xx.filmnest.data.local.EmbeddingDao
import com.cross19xx.filmnest.data.local.FILM_NEST_DB_NAME
import com.cross19xx.filmnest.data.local.FilmNestDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmbeddingModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FilmNestDatabase =
        Room.databaseBuilder(
            context,
            FilmNestDatabase::class.java, FILM_NEST_DB_NAME
        ).build()

    @Provides
    fun provideEmbeddingDao(database: FilmNestDatabase): EmbeddingDao = database.embeddingDao()

    @Provides
    @Singleton
    fun provideTextEmbedder(@ApplicationContext context: Context): TextEmbedder =
        MediaPipeTextEmbedder(context)

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
