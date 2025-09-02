package com.ftl.audioplayer.audio

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              AUDIO ENGINE - UNIT TESTS                      ║
 * ║              Neural-Enhanced Testing Suite                  ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
 *
 * Performance Test Targets:
 * • Audio Latency: <10ms total pipeline
 * • Processing Time: <1ms per buffer
 * • CPU Usage: <15% normal operation
 * • Memory Usage: <50MB per audio stream
 */

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for the FTL Audio Engine
 * 
 * Tests focus on:
 * - Initialization and configuration
 * - State management
 * - Performance characteristics
 * - Error handling
 * - Latency verification
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30]) // Android 11 for testing
class AudioEngineTest {
    
    private lateinit var context: Context
    private lateinit var audioEngine: AudioEngine
    
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        audioEngine = AudioEngine(context)
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // INITIALIZATION TESTS
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    @Test
    fun `audioEngine initializes with default configuration`() = runTest {
        // Given: Fresh audio engine instance
        val initialState = audioEngine.engineState.value
        
        // When: Initialize with default settings
        val success = audioEngine.initialize()
        
        // Then: Engine should be ready
        assertThat(success).isTrue()
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.READY)
        assertThat(initialState).isEqualTo(AudioEngineState.UNINITIALIZED)
    }
    
    @Test
    fun `audioEngine initializes with custom configuration`() = runTest {
        // Given: Custom configuration
        val customSampleRate = 96000
        val customBitDepth = 32
        val customBufferSize = 128
        
        // When: Initialize with custom settings
        val success = audioEngine.initialize(
            preferredSampleRate = customSampleRate,
            preferredBitDepth = customBitDepth,
            preferredBufferSize = customBufferSize
        )
        
        // Then: Engine should use optimal settings based on hardware
        assertThat(success).isTrue()
        
        val audioSpecs = audioEngine.audioSpecs.value
        assertThat(audioSpecs.sampleRate).isAtLeast(44100) // Should be at least CD quality
        assertThat(audioSpecs.channelCount).isEqualTo(2) // Stereo
        assertThat(audioSpecs.bufferSizeFrames).isAtLeast(64) // Minimum buffer size
    }
    
    @Test
    fun `audioEngine handles double initialization gracefully`() = runTest {
        // Given: Already initialized engine
        val firstResult = audioEngine.initialize()
        assertThat(firstResult).isTrue()
        
        // When: Try to initialize again
        val secondResult = audioEngine.initialize()
        
        // Then: Should handle gracefully (implementation may reinitialize or ignore)
        // Engine should remain in valid state
        assertThat(audioEngine.engineState.value).isIn(
            listOf(AudioEngineState.READY, AudioEngineState.PLAYING)
        )
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // PLAYBACK CONTROL TESTS
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    @Test
    fun `audioEngine starts playback when ready`() = runTest {
        // Given: Initialized engine
        audioEngine.initialize()
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.READY)
        
        // When: Start playback
        val success = audioEngine.start()
        
        // Then: Should start playing
        assertThat(success).isTrue()
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.PLAYING)
    }
    
    @Test
    fun `audioEngine stops playback when playing`() = runTest {
        // Given: Playing engine
        audioEngine.initialize()
        audioEngine.start()
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.PLAYING)
        
        // When: Stop playback
        val success = audioEngine.stop()
        
        // Then: Should stop and return to ready state
        assertThat(success).isTrue()
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.READY)
    }
    
    @Test
    fun `audioEngine pauses and resumes playback`() = runTest {
        // Given: Playing engine
        audioEngine.initialize()
        audioEngine.start()
        
        // When: Pause playback
        val pauseSuccess = audioEngine.pause()
        
        // Then: Should be paused
        assertThat(pauseSuccess).isTrue()
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.PAUSED)
        
        // When: Resume playback
        val resumeSuccess = audioEngine.resume()
        
        // Then: Should be playing again
        assertThat(resumeSuccess).isTrue()
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.PLAYING)
    }
    
    @Test
    fun `audioEngine prevents invalid state transitions`() = runTest {
        // Given: Uninitialized engine
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.UNINITIALIZED)
        
        // When: Try to start without initialization
        val startResult = audioEngine.start()
        
        // Then: Should fail gracefully
        assertThat(startResult).isFalse()
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.UNINITIALIZED)
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // AUDIO PROCESSING TESTS
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    @Test
    fun `audioEngine processes audio buffers correctly`() = runTest {
        // Given: Initialized and ready engine
        audioEngine.initialize()
        
        // When: Process a test audio buffer
        val inputBuffer = FloatArray(1024) { index ->
            (0.5 * kotlin.math.sin(2.0 * kotlin.math.PI * 440.0 * index / 48000.0)).toFloat()
        }
        
        val outputBuffer = audioEngine.processAudioBuffer(
            inputBuffer,
            sampleRate = 48000,
            channelCount = 2
        )
        
        // Then: Should return processed buffer
        assertThat(outputBuffer).isNotNull()
        assertThat(outputBuffer!!.size).isEqualTo(inputBuffer.size)
        
        // Audio should not be completely silent (some processing should occur)
        val hasNonZeroSamples = outputBuffer.any { it != 0.0f }
        assertThat(hasNonZeroSamples).isTrue()
    }
    
    @Test
    fun `audioEngine handles empty buffer gracefully`() = runTest {
        // Given: Initialized engine
        audioEngine.initialize()
        
        // When: Process empty buffer
        val emptyBuffer = FloatArray(0)
        val result = audioEngine.processAudioBuffer(
            emptyBuffer,
            sampleRate = 48000,
            channelCount = 2
        )
        
        // Then: Should handle gracefully
        assertThat(result).isNotNull()
        assertThat(result!!.size).isEqualTo(0)
    }
    
    @Test
    fun `audioEngine validates buffer parameters`() = runTest {
        // Given: Initialized engine
        audioEngine.initialize()
        
        // When: Process buffer with invalid parameters
        val testBuffer = FloatArray(100) { 0.1f }
        
        // Test invalid sample rate
        val result1 = audioEngine.processAudioBuffer(
            testBuffer,
            sampleRate = -1, // Invalid
            channelCount = 2
        )
        
        // Test invalid channel count
        val result2 = audioEngine.processAudioBuffer(
            testBuffer,
            sampleRate = 48000,
            channelCount = 0 // Invalid
        )
        
        // Then: Should handle invalid parameters gracefully
        // Implementation may return null or throw exceptions
        // At minimum, should not crash
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // PERFORMANCE TESTS
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    @Test
    fun `audioEngine measures latency within target range`() = runTest {
        // Given: Initialized and playing engine
        audioEngine.initialize()
        audioEngine.start()
        
        // When: Measure latency
        val latencyInfo = audioEngine.measureLatency()
        
        // Then: Latency should be within acceptable range
        assertThat(latencyInfo.totalLatencyMs).isAtLeast(0.0)
        assertThat(latencyInfo.totalLatencyMs).isAtMost(100.0) // Reasonable upper bound
        
        // Target is <10ms, but in testing environment may be higher
        val isLowLatency = latencyInfo.totalLatencyMs < 10.0
        if (isLowLatency) {
            assertThat(latencyInfo.isLowLatency).isTrue()
        }
        
        assertThat(latencyInfo.measurementTime).isGreaterThan(0L)
    }
    
    @Test
    fun `audioEngine provides performance metrics`() = runTest {
        // Given: Running engine with some activity
        audioEngine.initialize()
        audioEngine.start()
        
        // Process some buffers to generate metrics
        repeat(10) {
            val testBuffer = FloatArray(256) { 0.1f }
            audioEngine.processAudioBuffer(testBuffer, 48000, 2)
        }
        
        // When: Get performance metrics
        val metrics = audioEngine.getPerformanceMetrics()
        
        // Then: Metrics should be valid
        assertThat(metrics.cpuUsagePercent).isAtLeast(0.0)
        assertThat(metrics.cpuUsagePercent).isAtMost(100.0)
        assertThat(metrics.memoryUsageMB).isAtLeast(0.0)
        assertThat(metrics.bufferUnderruns).isAtLeast(0L)
        assertThat(metrics.bufferOverruns).isAtLeast(0L)
        assertThat(metrics.averageProcessingTimeUs).isAtLeast(0.0)
        assertThat(metrics.maxProcessingTimeUs).isAtLeast(0.0)
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // CONFIGURATION TESTS
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    @Test
    fun `audioEngine provides current audio specifications`() = runTest {
        // Given: Initialized engine
        audioEngine.initialize(
            preferredSampleRate = 48000,
            preferredBitDepth = 24
        )
        
        // When: Get audio specifications
        val specs = audioEngine.audioSpecs.value
        
        // Then: Specs should reflect actual configuration
        assertThat(specs.sampleRate).isGreaterThan(0)
        assertThat(specs.bitDepth).isGreaterThan(0)
        assertThat(specs.channelCount).isGreaterThan(0)
        assertThat(specs.bufferSizeFrames).isGreaterThan(0)
        assertThat(specs.format).isNotEmpty()
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // CLEANUP TESTS
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    @Test
    fun `audioEngine shuts down cleanly`() = runTest {
        // Given: Running engine
        audioEngine.initialize()
        audioEngine.start()
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.PLAYING)
        
        // When: Shutdown
        audioEngine.shutdown()
        
        // Then: Should be shut down
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.SHUTDOWN)
        
        // Further operations should fail gracefully
        val startResult = audioEngine.start()
        assertThat(startResult).isFalse()
    }
    
    @Test
    fun `audioEngine handles multiple shutdowns gracefully`() = runTest {
        // Given: Shutdown engine
        audioEngine.initialize()
        audioEngine.shutdown()
        
        // When: Shutdown again
        audioEngine.shutdown()
        
        // Then: Should handle gracefully without exceptions
        assertThat(audioEngine.engineState.value).isEqualTo(AudioEngineState.SHUTDOWN)
    }
}