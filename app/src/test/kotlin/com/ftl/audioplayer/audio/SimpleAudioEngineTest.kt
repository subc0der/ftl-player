package com.ftl.audioplayer.audio

import org.junit.Test
import com.google.common.truth.Truth.assertThat

/**
 * Simple audio engine tests that don't require Robolectric
 * These tests verify basic functionality without Android context
 */
class SimpleAudioEngineTest {
    
    @Test
    fun `audioEngineState enum has correct values`() {
        val states = AudioEngineState.values().toList()
        
        assertThat(states).containsExactly(
            AudioEngineState.UNINITIALIZED,
            AudioEngineState.INITIALIZING,
            AudioEngineState.READY,
            AudioEngineState.PLAYING,
            AudioEngineState.PAUSED,
            AudioEngineState.ERROR,
            AudioEngineState.SHUTDOWN
        ).inOrder()
    }
    
    @Test
    fun `audioSpecs data class has default values`() {
        val specs = AudioSpecs()
        
        assertThat(specs.sampleRate).isEqualTo(0)
        assertThat(specs.bitDepth).isEqualTo(0)
        assertThat(specs.channelCount).isEqualTo(0)
        assertThat(specs.bufferSizeFrames).isEqualTo(0)
        assertThat(specs.format).isEqualTo("UNKNOWN")
    }
    
    @Test
    fun `latencyInfo data class calculates correctly`() {
        val latency = LatencyInfo(
            totalLatencyMs = 8.5,
            inputLatencyMs = 2.0,
            outputLatencyMs = 6.5,
            isLowLatency = true,
            measurementTime = System.currentTimeMillis()
        )
        
        assertThat(latency.totalLatencyMs).isEqualTo(8.5)
        assertThat(latency.isLowLatency).isTrue()
        assertThat(latency.measurementTime).isGreaterThan(0L)
    }
    
    @Test
    fun `performanceMetrics data class has correct fields`() {
        val metrics = PerformanceMetrics(
            cpuUsagePercent = 12.5,
            memoryUsageMB = 45.0,
            bufferUnderruns = 2L,
            bufferOverruns = 1L,
            averageProcessingTimeUs = 750.0,
            maxProcessingTimeUs = 1200.0,
            callbackCount = 1000L,
            missedCallbacks = 3L,
            callbackLoad = 75.0
        )
        
        assertThat(metrics.cpuUsagePercent).isEqualTo(12.5)
        assertThat(metrics.callbackCount).isEqualTo(1000L)
        assertThat(metrics.callbackLoad).isEqualTo(75.0)
    }
    
    @Test
    fun `audioEngineConfiguration has sensible defaults`() {
        val config = AudioEngineConfiguration()
        
        assertThat(config.enableLowLatencyMode).isTrue()
        assertThat(config.enableHighResolution).isFalse()
        assertThat(config.enableDSPProcessing).isTrue()
        assertThat(config.bufferSizeMultiplier).isEqualTo(1.0f)
        assertThat(config.threadPriority).isEqualTo(-19) // THREAD_PRIORITY_URGENT_AUDIO
    }
}