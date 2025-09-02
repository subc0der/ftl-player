package com.ftl.audioplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                FTL AUDIO PLAYER APPLICATION                  ║
 * ║           AI-Powered Hi-Res Audio Player                    ║
 * ╚══════════════════════════════════════════════════════════════╝
 * 
 * Main application class for the FTL Hi-Res Audio Player.
 * Initializes Hilt dependency injection and global application state.
 */
@HiltAndroidApp
class FTLAudioPlayerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Application initialization will be handled by Hilt modules
    }
}