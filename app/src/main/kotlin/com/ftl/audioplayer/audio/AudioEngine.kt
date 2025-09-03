package com.ftl.audioplayer.audio

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                FTL AUDIO ENGINE - CORE CLASS                ║
 * ║           Neural-Enhanced Real-Time Audio Processing        ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
 *
 * Performance Targets:
 * • Total Latency: <10ms end-to-end
 * • THD+N: <0.001% @ 1kHz, -60dB  
 * • Sample Rates: 44.1kHz to 768kHz
 * • Bit Depth: 16, 24, 32-bit float
 * • DSD Support: DSD64, DSD128, DSD256, DSD512
 * • CPU Usage: <15% normal operation
 */

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Core audio engine managing all audio processing operations
 * 
 * Architecture:
 * - Kotlin Interface Layer (this class)
 * - JNI Bridge (native calls)
 * - C++ Processing Core (real-time DSP)
 * - Platform Layer (AAudio/OpenSL ES)
 */
@Singleton
class AudioEngine @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "AudioEngine"
        
        // Performance constants
        const val TARGET_LATENCY_MS = 10
        const val MIN_BUFFER_SIZE_FRAMES = 64
        const val MAX_BUFFER_SIZE_FRAMES = 2048
        const val DEFAULT_SAMPLE_RATE = 48000
        const val DEFAULT_BIT_DEPTH = 24
        
        // Audio configuration constants
        private const val DEFAULT_BUFFER_SIZE_FRAMES = 256
        private const val STEREO_CHANNEL_COUNT = 2
        private const val DEFAULT_DEVICE_ID = 0
        
        // Load native library
        init {
            try {
                System.loadLibrary("ftl_audio_engine")
                Log.i(TAG, "Native audio library loaded successfully")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load native audio library: ${e.message}")
                throw RuntimeException("Failed to load ftl_audio_engine native library", e)
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // STATE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    private val _engineState = MutableStateFlow(AudioEngineState.UNINITIALIZED)
    val engineState: StateFlow<AudioEngineState> = _engineState.asStateFlow()
    
    private val _audioSpecs = MutableStateFlow(AudioSpecs())
    val audioSpecs: StateFlow<AudioSpecs> = _audioSpecs.asStateFlow()
    
    private val _latencyInfo = MutableStateFlow(LatencyInfo())
    val latencyInfo: StateFlow<LatencyInfo> = _latencyInfo.asStateFlow()
    
    private val _performanceMetrics = MutableStateFlow(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()
    
    // Native engine handle (opaque pointer)
    private var nativeEngineHandle: Long = 0
    
    // Audio manager for system integration
    private val audioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // INITIALIZATION
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    /**
     * Initialize the audio engine with optimal settings
     * 
     * @param preferredSampleRate Target sample rate (Hz)
     * @param preferredBitDepth Target bit depth (16, 24, 32)
     * @param preferredBufferSize Target buffer size in frames
     * @return True if initialization successful
     */
    suspend fun initialize(
        preferredSampleRate: Int = DEFAULT_SAMPLE_RATE,
        preferredBitDepth: Int = DEFAULT_BIT_DEPTH,
        preferredBufferSize: Int = 0 // 0 = auto-detect optimal
    ): Boolean = suspendCoroutine { continuation ->
        
        _engineState.value = AudioEngineState.INITIALIZING
        
        try {
            // Get optimal audio configuration from system
            val optimalConfig = getOptimalAudioConfiguration(
                preferredSampleRate, 
                preferredBitDepth, 
                preferredBufferSize
            )
            
            // Initialize native audio engine
            val initResult = nativeInitializeEngine(
                sampleRate = optimalConfig.sampleRate,
                framesPerBurst = optimalConfig.framesPerBurst,
                channelCount = optimalConfig.channelCount,
                format = optimalConfig.format,
                deviceId = optimalConfig.deviceId
            )
            
            if (initResult > 0) {
                nativeEngineHandle = initResult
                
                // Update audio specs
                _audioSpecs.value = AudioSpecs(
                    sampleRate = optimalConfig.sampleRate,
                    bitDepth = optimalConfig.bitDepth,
                    channelCount = optimalConfig.channelCount,
                    bufferSizeFrames = optimalConfig.framesPerBurst,
                    format = mapNativeFormat(optimalConfig.format)
                )
                
                // Measure initial latency (async)
                // This will be measured when needed
                
                _engineState.value = AudioEngineState.READY
                continuation.resume(true)
            } else {
                _engineState.value = AudioEngineState.ERROR
                continuation.resume(false)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception during audio engine initialization", e)
            _engineState.value = AudioEngineState.ERROR
            continuation.resume(false)
        }
    }
    
    /**
     * Get optimal audio configuration for the current device
     */
    private fun getOptimalAudioConfiguration(
        preferredSampleRate: Int,
        preferredBitDepth: Int,
        preferredBufferSize: Int
    ): OptimalAudioConfig {
        
        // Get system properties
        val nativeSampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)?.toInt()
            ?: DEFAULT_SAMPLE_RATE
            
        val framesPerBurst = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)?.toInt()
            ?: DEFAULT_BUFFER_SIZE_FRAMES
        
        // Choose optimal sample rate
        val optimalSampleRate = when {
            preferredSampleRate > 0 -> preferredSampleRate
            nativeSampleRate > 0 -> nativeSampleRate
            else -> DEFAULT_SAMPLE_RATE
        }
        
        // Choose optimal buffer size
        val optimalBufferSize = when {
            preferredBufferSize > 0 -> preferredBufferSize
            framesPerBurst > 0 -> framesPerBurst
            else -> DEFAULT_BUFFER_SIZE_FRAMES
        }.coerceIn(MIN_BUFFER_SIZE_FRAMES, MAX_BUFFER_SIZE_FRAMES)
        
        // Determine audio format
        val (format, bitDepth) = when (preferredBitDepth) {
            16 -> AudioFormat.ENCODING_PCM_16BIT to 16
            24 -> AudioFormat.ENCODING_PCM_24BIT_PACKED to 24
            32 -> AudioFormat.ENCODING_PCM_FLOAT to 32
            else -> AudioFormat.ENCODING_PCM_16BIT to 16
        }
        
        return OptimalAudioConfig(
            sampleRate = optimalSampleRate,
            framesPerBurst = optimalBufferSize,
            channelCount = STEREO_CHANNEL_COUNT,
            format = format,
            bitDepth = bitDepth,
            deviceId = DEFAULT_DEVICE_ID
        )
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // PLAYBACK CONTROL
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    /**
     * Start audio playback
     */
    suspend fun start(): Boolean {
        check(nativeEngineHandle != 0L) { "Audio engine not initialized" }
        if (_engineState.value != AudioEngineState.READY) return false
        
        val result = nativeStartPlayback(nativeEngineHandle)
        if (result) {
            _engineState.value = AudioEngineState.PLAYING
        }
        return result
    }
    
    /**
     * Stop audio playback
     */
    suspend fun stop(): Boolean {
        check(nativeEngineHandle != 0L) { "Audio engine not initialized" }
        if (_engineState.value != AudioEngineState.PLAYING) return false
        
        val result = nativeStopPlayback(nativeEngineHandle)
        if (result) {
            _engineState.value = AudioEngineState.READY
        }
        return result
    }
    
    /**
     * Pause audio playback
     */
    suspend fun pause(): Boolean {
        check(nativeEngineHandle != 0L) { "Audio engine not initialized" }
        if (_engineState.value != AudioEngineState.PLAYING) return false
        
        val result = nativePausePlayback(nativeEngineHandle)
        if (result) {
            _engineState.value = AudioEngineState.PAUSED
        }
        return result
    }
    
    /**
     * Resume audio playback
     */
    suspend fun resume(): Boolean {
        check(nativeEngineHandle != 0L) { "Audio engine not initialized" }
        if (_engineState.value != AudioEngineState.PAUSED) return false
        
        val result = nativeResumePlayback(nativeEngineHandle)
        if (result) {
            _engineState.value = AudioEngineState.PLAYING
        }
        return result
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // AUDIO DATA PROCESSING
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    /**
     * Process audio buffer through the engine
     * 
     * @param audioData Input audio samples
     * @param sampleRate Sample rate of input data
     * @param channelCount Number of audio channels
     * @return Processed audio samples
     */
    suspend fun processAudioBuffer(
        audioData: FloatArray,
        sampleRate: Int,
        channelCount: Int
    ): FloatArray? {
        if (audioData.isEmpty() || sampleRate <= 0 || channelCount <= 0) {
            return null
        }
        
        if (_engineState.value != AudioEngineState.PLAYING && 
            _engineState.value != AudioEngineState.READY) {
            return null
        }
        
        return nativeProcessAudioBuffer(
            nativeEngineHandle, 
            audioData, 
            audioData.size,
            sampleRate,
            channelCount
        )
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // PERFORMANCE MONITORING
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    /**
     * Measure current audio latency
     */
    suspend fun measureLatency(): LatencyInfo {
        val latencyMs = if (nativeEngineHandle != 0L) {
            nativeMeasureLatency(nativeEngineHandle)
        } else {
            -1.0
        }
        
        val latencyInfo = LatencyInfo(
            totalLatencyMs = latencyMs,
            inputLatencyMs = latencyMs * 0.3, // Estimated
            outputLatencyMs = latencyMs * 0.7, // Estimated
            isLowLatency = latencyMs < TARGET_LATENCY_MS,
            measurementTime = System.currentTimeMillis()
        )
        
        _latencyInfo.value = latencyInfo
        return latencyInfo
    }
    
    /**
     * Get current performance metrics
     */
    suspend fun getPerformanceMetrics(): PerformanceMetrics {
        val metrics = if (nativeEngineHandle != 0L) {
            nativeGetPerformanceMetrics(nativeEngineHandle)
        } else {
            PerformanceMetrics()
        }
        
        _performanceMetrics.value = metrics
        return metrics
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // CONFIGURATION
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    /**
     * Update audio engine configuration
     */
    suspend fun updateConfiguration(config: AudioEngineConfiguration): Boolean {
        return if (nativeEngineHandle != 0L) {
            nativeUpdateConfiguration(nativeEngineHandle, config)
        } else {
            false
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // CLEANUP
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    /**
     * Shutdown the audio engine and release resources
     */
    suspend fun shutdown() {
        if (nativeEngineHandle != 0L) {
            nativeShutdownEngine(nativeEngineHandle)
            nativeEngineHandle = 0L
        }
        _engineState.value = AudioEngineState.SHUTDOWN
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // UTILITY FUNCTIONS
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    private fun mapNativeFormat(format: Int): String = when (format) {
        AudioFormat.ENCODING_PCM_16BIT -> "PCM_16"
        AudioFormat.ENCODING_PCM_24BIT_PACKED -> "PCM_24"
        AudioFormat.ENCODING_PCM_FLOAT -> "PCM_FLOAT32"
        else -> "UNKNOWN"
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════════
    // NATIVE METHOD DECLARATIONS
    // ═══════════════════════════════════════════════════════════════════════════════════
    
    /**
     * Initialize native audio engine
     * @return Native engine handle (> 0) or error code (<= 0)
     */
    private external fun nativeInitializeEngine(
        sampleRate: Int,
        framesPerBurst: Int,
        channelCount: Int,
        format: Int,
        deviceId: Int
    ): Long
    
    /**
     * Start playback on native engine
     */
    private external fun nativeStartPlayback(engineHandle: Long): Boolean
    
    /**
     * Stop playback on native engine
     */
    private external fun nativeStopPlayback(engineHandle: Long): Boolean
    
    /**
     * Pause playback on native engine
     */
    private external fun nativePausePlayback(engineHandle: Long): Boolean
    
    /**
     * Resume playback on native engine
     */
    private external fun nativeResumePlayback(engineHandle: Long): Boolean
    
    /**
     * Process audio buffer through native engine
     */
    private external fun nativeProcessAudioBuffer(
        engineHandle: Long,
        inputBuffer: FloatArray,
        bufferSize: Int,
        sampleRate: Int,
        channelCount: Int
    ): FloatArray?
    
    /**
     * Measure audio latency in native engine
     */
    private external fun nativeMeasureLatency(engineHandle: Long): Double
    
    /**
     * Get performance metrics from native engine
     */
    private external fun nativeGetPerformanceMetrics(engineHandle: Long): PerformanceMetrics
    
    /**
     * Update native engine configuration
     */
    private external fun nativeUpdateConfiguration(
        engineHandle: Long, 
        config: AudioEngineConfiguration
    ): Boolean
    
    /**
     * Shutdown native audio engine
     */
    private external fun nativeShutdownEngine(engineHandle: Long)
}

// ═══════════════════════════════════════════════════════════════════════════════════
// DATA CLASSES & ENUMS
// ═══════════════════════════════════════════════════════════════════════════════════

enum class AudioEngineState {
    UNINITIALIZED,
    INITIALIZING, 
    READY,
    PLAYING,
    PAUSED,
    ERROR,
    SHUTDOWN
}

data class AudioSpecs(
    val sampleRate: Int = 0,
    val bitDepth: Int = 0,
    val channelCount: Int = 0,
    val bufferSizeFrames: Int = 0,
    val format: String = "UNKNOWN"
)

data class LatencyInfo(
    val totalLatencyMs: Double = 0.0,
    val inputLatencyMs: Double = 0.0,
    val outputLatencyMs: Double = 0.0,
    val isLowLatency: Boolean = false,
    val measurementTime: Long = 0L
)

data class PerformanceMetrics(
    val cpuUsagePercent: Double = 0.0,
    val memoryUsageMB: Double = 0.0,
    val bufferUnderruns: Long = 0L,
    val bufferOverruns: Long = 0L,
    val averageProcessingTimeUs: Double = 0.0,
    val maxProcessingTimeUs: Double = 0.0,
    val callbackCount: Long = 0L,
    val missedCallbacks: Long = 0L,
    val callbackLoad: Double = 0.0
)

data class AudioEngineConfiguration(
    val enableLowLatencyMode: Boolean = true,
    val enableHighResolution: Boolean = false,
    val enableDSPProcessing: Boolean = true,
    val bufferSizeMultiplier: Float = 1.0f,
    val threadPriority: Int = -19 // THREAD_PRIORITY_URGENT_AUDIO
)

private data class OptimalAudioConfig(
    val sampleRate: Int,
    val framesPerBurst: Int,
    val channelCount: Int,
    val format: Int,
    val bitDepth: Int,
    val deviceId: Int
)