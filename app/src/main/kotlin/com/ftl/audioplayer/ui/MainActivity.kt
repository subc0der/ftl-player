package com.ftl.audioplayer.ui

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              FTL AUDIO PLAYER - MAIN ACTIVITY               ║
 * ║              Neural-Enhanced Cyberpunk Interface            ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
 */

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ftl.audioplayer.ui.navigation.FTLNavigation
import com.ftl.audioplayer.ui.theme.FTLAudioTheme
import com.ftl.audioplayer.service.FTLAudioService
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity showcasing the FTL Audio Engine
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "FTL_MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.i(TAG, "🎵 FTL Audio Player UI starting...")
        
        setContent {
            FTLAudioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black // Cyberpunk background
                ) {
                    FTLNavigation()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "🛑 MainActivity destroyed - stopping audio service")
        // Stop the audio service when the app is closed to clear lock screen controls
        stopService(Intent(this, FTLAudioService::class.java))
    }
}

