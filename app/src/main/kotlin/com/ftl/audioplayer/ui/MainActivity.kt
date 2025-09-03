package com.ftl.audioplayer.ui

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              FTL AUDIO PLAYER - MAIN ACTIVITY               ║
 * ║              Neural-Enhanced Cyberpunk Interface            ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
 */

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
                    AudioEngineTestScreen()
                }
            }
        }
    }
}

/**
 * Test screen to verify audio engine functionality
 */
@Composable
fun AudioEngineTestScreen(
    viewModel: AudioEngineTestViewModel = hiltViewModel()
) {
    val engineState by viewModel.engineState.collectAsState()
    val audioSpecs by viewModel.audioSpecs.collectAsState()
    val latencyInfo by viewModel.latencyInfo.collectAsState()
    val performanceMetrics by viewModel.performanceMetrics.collectAsState()
    
    // Initialize audio engine on first composition
    LaunchedEffect(Unit) {
        viewModel.initializeAudioEngine()
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buildString {
                appendLine("🎵 FTL HI-RES AUDIO PLAYER")
                appendLine("Neural-Enhanced Audio Engine")
                appendLine("")
                appendLine("════════════════════════════")
                appendLine("ENGINE STATUS")
                appendLine("════════════════════════════")
                appendLine("State: $engineState")
                appendLine("Sample Rate: ${audioSpecs.sampleRate} Hz")
                appendLine("Bit Depth: ${audioSpecs.bitDepth} bit")
                appendLine("Channels: ${audioSpecs.channelCount}")
                appendLine("Buffer: ${audioSpecs.bufferSizeFrames} frames")
                appendLine("Format: ${audioSpecs.format}")
                appendLine("")
                appendLine("════════════════════════════")
                appendLine("LATENCY ANALYSIS")
                appendLine("════════════════════════════")
                appendLine("Total: %.2f ms".format(latencyInfo.totalLatencyMs))
                appendLine("Input: %.2f ms".format(latencyInfo.inputLatencyMs))
                appendLine("Output: %.2f ms".format(latencyInfo.outputLatencyMs))
                appendLine("Low Latency: ${if (latencyInfo.isLowLatency) "✅ YES" else "❌ NO"}")
                appendLine("Target: <10ms")
                appendLine("")
                appendLine("════════════════════════════")
                appendLine("PERFORMANCE METRICS")
                appendLine("════════════════════════════")
                appendLine("CPU Usage: %.2f%%".format(performanceMetrics.cpuUsagePercent))
                appendLine("Memory: %.2f MB".format(performanceMetrics.memoryUsageMB))
                appendLine("Callback Count: ${performanceMetrics.callbackCount}")
                appendLine("Underruns: ${performanceMetrics.bufferUnderruns}")
                appendLine("Overruns: ${performanceMetrics.bufferOverruns}")
                appendLine("Avg Processing: %.2f μs".format(performanceMetrics.averageProcessingTimeUs))
                appendLine("Max Processing: %.2f μs".format(performanceMetrics.maxProcessingTimeUs))
            },
            color = Color(0xFF00FFFF), // Cyber Aqua
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * Cyberpunk theme for the application
 */
@Composable
fun FTLAudioTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}