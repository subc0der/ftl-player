package com.ftl.audioplayer.di

import android.content.Context
import com.ftl.audioplayer.ai.NeuralAudioProcessor
import com.ftl.audioplayer.ai.TensorFlowLiteModelManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                     AI MODULE                                ║
 * ║           Dependency Injection for AI Components            ║
 * ╚══════════════════════════════════════════════════════════════╝
 * 
 * Hilt module providing AI-related dependencies including
 * TensorFlow Lite model management and neural audio processing.
 */
@Module
@InstallIn(SingletonComponent::class)
object AIModule {
    
    @Provides
    @Singleton
    fun provideTensorFlowLiteModelManager(
        @ApplicationContext context: Context
    ): TensorFlowLiteModelManager {
        return TensorFlowLiteModelManager(context)
    }
    
    @Provides
    @Singleton
    fun provideNeuralAudioProcessor(
        modelManager: TensorFlowLiteModelManager
    ): NeuralAudioProcessor {
        return NeuralAudioProcessor(modelManager)
    }
}