package com.ftl.audioplayer.di

import android.content.Context
import com.ftl.audioplayer.data.dao.PlaylistDao
import com.ftl.audioplayer.data.dao.TrackDao
import com.ftl.audioplayer.data.database.MusicDatabase
import com.ftl.audioplayer.data.repository.MusicRepository
import com.ftl.audioplayer.playback.MusicPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideMusicDatabase(@ApplicationContext context: Context): MusicDatabase {
        return MusicDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideTrackDao(database: MusicDatabase): TrackDao {
        return database.trackDao()
    }
    
    @Provides
    fun providePlaylistDao(database: MusicDatabase): PlaylistDao {
        return database.playlistDao()
    }
    
    @Provides
    @Singleton
    fun provideMusicRepository(
        trackDao: TrackDao,
        playlistDao: PlaylistDao,
        @ApplicationContext context: Context
    ): MusicRepository {
        return MusicRepository(trackDao, playlistDao, context)
    }
    
    @Provides
    @Singleton
    fun provideMusicPlayer(
        @ApplicationContext context: Context
    ): MusicPlayer {
        return MusicPlayer(context)
    }
}