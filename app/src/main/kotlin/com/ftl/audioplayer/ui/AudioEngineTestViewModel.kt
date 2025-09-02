package com.ftl.audioplayer.ui

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║           AUDIO ENGINE TEST - VIEW MODEL                    ║
 * ║              Real-time Performance Monitoring               ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.ftl.audioplayer.audio.AudioEngine
import com.ftl.audioplayer.audio.AudioEngineState
import com.ftl.audioplayer.audio.AudioSpecs
import com.ftl.audioplayer.audio.LatencyInfo
import com.ftl.audioplayer.audio.PerformanceMetrics
import javax.inject.Inject

/**
 * ViewModel for testing and monitoring audio engine performance
 */
@HiltViewModel
class AudioEngineTestViewModel @Inject constructor(
    private val audioEngine: AudioEngine
) : ViewModel() {
    
    companion object {
        private const val TAG = "AudioEngineTest"
        private const val METRICS_UPDATE_INTERVAL_MS = 1000L // Update every second
    }
    
    // Expose audio engine state flows
    val engineState: StateFlow<AudioEngineState> = audioEngine.engineState
    val audioSpecs: StateFlow<AudioSpecs> = audioEngine.audioSpecs
    val latencyInfo: StateFlow<LatencyInfo> = audioEngine.latencyInfo
    val performanceMetrics: StateFlow<PerformanceMetrics> = audioEngine.performanceMetrics
    
    private val _testStatus = MutableStateFlow("Initializing...")
    val testStatus: StateFlow<String> = _testStatus.asStateFlow()
    
    private var isMonitoring = false
    
    /**
     * Initialize the audio engine for testing
     */
    suspend fun initializeAudioEngine() {
        try {
            Log.i(TAG, "Initializing FTL Audio Engine for testing...")
            _testStatus.value = "Initializing Audio Engine..."
            
            // Initialize with optimal settings for testing
            val success = audioEngine.initialize(
                preferredSampleRate = 48000,
                preferredBitDepth = 24,
                preferredBufferSize = 256 // Small buffer for low latency
            )
            
            if (success) {
                Log.i(TAG, "✅ Audio engine initialized successfully")
                _testStatus.value = "Audio Engine Ready"
                
                // Start performance monitoring
                startPerformanceMonitoring()
                
                // Start audio playback for testing
                startTestPlayback()
                
            } else {
                Log.e(TAG, "❌ Failed to initialize audio engine")
                _testStatus.value = "Initialization Failed"
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception during initialization", e)
            _testStatus.value = "Error: ${e.message}"
        }
    }
    
    /**
     * Start audio playback for testing
     */
    private suspend fun startTestPlayback() {
        try {
            Log.i(TAG, "Starting test playback...")
            _testStatus.value = "Starting Playback..."
            
            val success = audioEngine.start()
            if (success) {
                Log.i(TAG, "✅ Test playback started")
                _testStatus.value = "Playing Test Audio"
                
                // Measure latency after a brief warmup
                delay(1000)
                measureLatency()
                
            } else {
                Log.e(TAG, "❌ Failed to start playback")
                _testStatus.value = "Playback Failed"
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception during playback start", e)
            _testStatus.value = "Playback Error: ${e.message}"
        }
    }
    
    /**
     * Measure and report audio latency
     */
    private suspend fun measureLatency() {
        try {
            Log.i(TAG, "Measuring audio latency...")
            
            val latency = audioEngine.measureLatency()
            val isLowLatency = latency.isLowLatency
            
            Log.i(TAG, "📊 Latency measurement:")
            Log.i(TAG, "   Total: %.2f ms".format(latency.totalLatencyMs))
            Log.i(TAG, "   Input: %.2f ms".format(latency.inputLatencyMs))
            Log.i(TAG, "   Output: %.2f ms".format(latency.outputLatencyMs))
            Log.i(TAG, "   Target: <10ms")
            Log.i(TAG, "   Status: ${if (isLowLatency) "✅ PASS" else "⚠️ HIGH"}")
            
            _testStatus.value = if (isLowLatency) {
                "✅ Low Latency Achieved (%.1fms)".format(latency.totalLatencyMs)
            } else {
                "⚠️ Latency High (%.1fms)".format(latency.totalLatencyMs)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception during latency measurement", e)
        }
    }
    
    /**
     * Start continuous performance monitoring
     */
    private fun startPerformanceMonitoring() {
        if (isMonitoring) return
        
        isMonitoring = true
        Log.i(TAG, "Starting performance monitoring...")
        
        viewModelScope.launch {
            while (isMonitoring) {
                try {
                    // Get current performance metrics
                    val metrics = audioEngine.getPerformanceMetrics()
                    
                    // Log performance data periodically
                    if (metrics.callbackCount % 48 == 0L) { // Every ~1 second at 48kHz
                        Log.d(TAG, "📊 Performance Update:")
                        Log.d(TAG, "   Callbacks: ${metrics.callbackCount}")
                        Log.d(TAG, "   Avg Processing: %.2f μs".format(metrics.averageProcessingTimeUs))
                        Log.d(TAG, "   Max Processing: %.2f μs".format(metrics.maxProcessingTimeUs))
                        Log.d(TAG, "   Underruns: ${metrics.bufferUnderruns}")
                        Log.d(TAG, "   CPU: %.2f%%".format(metrics.cpuUsagePercent))
                    }
                    
                    // Check for performance issues
                    if (metrics.averageProcessingTimeUs > 2000.0) { // > 2ms average
                        Log.w(TAG, "⚠️ High processing time detected: %.2f μs".format(metrics.averageProcessingTimeUs))
                    }
                    
                    if (metrics.bufferUnderruns > 0 && metrics.bufferUnderruns % 10 == 0L) {
                        Log.w(TAG, "⚠️ Buffer underruns detected: ${metrics.bufferUnderruns}")
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in performance monitoring", e)
                }
                
                delay(METRICS_UPDATE_INTERVAL_MS)
            }
        }
    }
    
    /**
     * Stop performance monitoring
     */
    private fun stopPerformanceMonitoring() {
        isMonitoring = false
        Log.i(TAG, "Performance monitoring stopped")
    }
    
    /**
     * Run a comprehensive audio engine test
     */
    fun runAudioEngineTest() {
        viewModelScope.launch {
            try {
                Log.i(TAG, "🧪 Starting comprehensive audio engine test...")
                _testStatus.value = "Running Comprehensive Test..."
                
                // Test 1: Latency verification
                measureLatency()
                delay(1000)
                
                // Test 2: Buffer processing test
                testBufferProcessing()
                delay(1000)
                
                // Test 3: Performance stress test
                testPerformanceStress()
                delay(1000)
                
                _testStatus.value = "✅ Test Complete"
                Log.i(TAG, "🎉 Audio engine test completed successfully")
                
            } catch (e: Exception) {
                Log.e(TAG, "Test failed with exception", e)
                _testStatus.value = "❌ Test Failed: ${e.message}"
            }
        }
    }
    
    /**
     * Test buffer processing functionality
     */
    private suspend fun testBufferProcessing() {
        Log.i(TAG, "Testing audio buffer processing...")
        
        try {
            // Create test audio buffer (1000 samples, stereo, 48kHz)
            val testBuffer = FloatArray(2000) { index ->
                // Generate a 440Hz sine wave
                val sample = index / 2 // Sample index (stereo)
                (0.5 * kotlin.math.sin(2.0 * kotlin.math.PI * 440.0 * sample / 48000.0)).toFloat()
            }
            
            val processedBuffer = audioEngine.processAudioBuffer(
                testBuffer, 
                48000, 
                2
            )
            
            if (processedBuffer != null && processedBuffer.size == testBuffer.size) {
                Log.i(TAG, "✅ Buffer processing test passed")
            } else {
                Log.e(TAG, "❌ Buffer processing test failed")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Buffer processing test exception", e)
        }
    }
    
    /**
     * Test performance under stress conditions
     */
    private suspend fun testPerformanceStress() {
        Log.i(TAG, "Running performance stress test...")
        
        // Process multiple buffers rapidly to test performance
        repeat(100) { iteration ->
            val testBuffer = FloatArray(1024) { 0.1f } // Small test buffer
            audioEngine.processAudioBuffer(testBuffer, 48000, 2)
            
            if (iteration % 20 == 0) {
                val metrics = audioEngine.getPerformanceMetrics()
                Log.d(TAG, "Stress test iteration $iteration: Avg processing ${metrics.averageProcessingTimeUs} μs")
            }
        }
        
        Log.i(TAG, "✅ Performance stress test completed")
    }
    
    override fun onCleared() {
        super.onCleared()
        
        // Stop monitoring and clean up
        stopPerformanceMonitoring()
        
        viewModelScope.launch {
            try {
                audioEngine.stop()
                audioEngine.shutdown()
                Log.i(TAG, "Audio engine cleaned up")
            } catch (e: Exception) {
                Log.e(TAG, "Exception during cleanup", e)
            }
        }
    }
}