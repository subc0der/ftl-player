package com.ftl.audioplayer.di

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              AUDIO MODULE - DEPENDENCY INJECTION            ║
 * ║              Neural-Enhanced Audio System DI                ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
 */

import android.content.Context
import com.ftl.audioplayer.audio.AudioEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing audio-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AudioModule {
    
    /**
     * Provides the main AudioEngine instance
     * Singleton to ensure single audio stream management
     */
    @Provides
    @Singleton
    fun provideAudioEngine(
        @ApplicationContext context: Context
    ): AudioEngine {
        return AudioEngine(context)
    }
}