/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║           FTL AUDIO ENGINE - CORE IMPLEMENTATION            ║
 * ║           Neural-Enhanced Real-Time Audio Processing        ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
 */

#include "FTLAudioEngine.h"
#include <android/log.h>
#include <unistd.h>
#include <cmath>
#include <algorithm>
#include <cstring>

#define LOG_TAG "FTL_AudioEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

namespace ftl_audio {

// ═══════════════════════════════════════════════════════════════════════════════════
// CONSTRUCTOR & DESTRUCTOR
// ═══════════════════════════════════════════════════════════════════════════════════

FTLAudioEngine::FTLAudioEngine() {
    LOGI("FTL Audio Engine created - Target latency: <10ms");
    m_startTime = std::chrono::high_resolution_clock::now();
}

FTLAudioEngine::~FTLAudioEngine() {
    shutdown();
    LOGI("FTL Audio Engine destroyed");
}

// ═══════════════════════════════════════════════════════════════════════════════════
// INITIALIZATION
// ═══════════════════════════════════════════════════════════════════════════════════

EngineResult FTLAudioEngine::initialize(const AudioEngineConfig& config) {
    if (m_engineState.load() != EngineState::UNINITIALIZED) {
        LOGE("Engine already initialized");
        return EngineResult::ERROR_ALREADY_RUNNING;
    }
    
    // Validate configuration
    auto result = validateConfiguration(config);
    if (result != EngineResult::SUCCESS) {
        LOGE("Invalid configuration provided");
        return result;
    }
    
    m_config = config;
    logConfiguration(config);
    
    // Setup AAudio stream
    result = setupAAudioStream();
    if (result != EngineResult::SUCCESS) {
        LOGE("Failed to setup AAudio stream");
        return result;
    }
    
    // Allocate audio buffer
    m_bufferSize = config.framesPerBurst * config.channelCount;
    m_audioBuffer = std::make_unique<float[]>(m_bufferSize);
    std::fill(m_audioBuffer.get(), m_audioBuffer.get() + m_bufferSize, 0.0f);
    
    // Initialize performance monitoring
    m_currentMetrics = PerformanceMetrics();
    m_lastCallbackTime = std::chrono::high_resolution_clock::now();
    
    m_engineState = EngineState::INITIALIZED;
    LOGI("FTL Audio Engine initialized successfully");
    
    return EngineResult::SUCCESS;
}

// ═══════════════════════════════════════════════════════════════════════════════════
// AAUDIO STREAM SETUP
// ═══════════════════════════════════════════════════════════════════════════════════

EngineResult FTLAudioEngine::setupAAudioStream() {
    // Create AAudio stream builder
    AAudioStreamBuilder* builder = nullptr;
    aaudio_result_t result = AAudio_createStreamBuilder(&builder);
    
    if (result != AAUDIO_OK) {
        LOGE("Failed to create AAudio stream builder: %s", AAudio_convertResultToText(result));
        return EngineResult::ERROR_HARDWARE_UNAVAILABLE;
    }
    
    // Configure stream builder
    AAudioStreamBuilder_setDeviceId(builder, m_config.deviceId);
    AAudioStreamBuilder_setDirection(builder, AAUDIO_DIRECTION_OUTPUT);
    AAudioStreamBuilder_setSampleRate(builder, m_config.sampleRate);
    AAudioStreamBuilder_setChannelCount(builder, m_config.channelCount);
    AAudioStreamBuilder_setFormat(builder, AAUDIO_FORMAT_PCM_FLOAT);
    
    // Performance optimization settings
    if (m_config.enableLowLatency) {
        AAudioStreamBuilder_setPerformanceMode(builder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    } else {
        AAudioStreamBuilder_setPerformanceMode(builder, AAUDIO_PERFORMANCE_MODE_NONE);
    }
    
    AAudioStreamBuilder_setSharingMode(builder, AAUDIO_SHARING_MODE_EXCLUSIVE);
    AAudioStreamBuilder_setBufferCapacityInFrames(builder, m_config.framesPerBurst * 2);
    AAudioStreamBuilder_setFramesPerDataCallback(builder, m_config.framesPerBurst);
    
    // Set callback functions
    AAudioStreamBuilder_setDataCallback(builder, audioCallback, this);
    AAudioStreamBuilder_setErrorCallback(builder, errorCallback, this);
    
    // Create the stream
    result = AAudioStreamBuilder_openStream(builder, &m_audioStream);
    AAudioStreamBuilder_delete(builder);
    
    if (result != AAUDIO_OK) {
        LOGE("Failed to open AAudio stream: %s", AAudio_convertResultToText(result));
        return EngineResult::ERROR_HARDWARE_UNAVAILABLE;
    }
    
    // Verify stream properties
    int actualSampleRate = AAudioStream_getSampleRate(m_audioStream);
    int actualChannelCount = AAudioStream_getChannelCount(m_audioStream);
    int actualFramesPerBurst = AAudioStream_getFramesPerBurst(m_audioStream);
    
    LOGI("Stream configured: SR=%d, Channels=%d, Frames=%d", 
         actualSampleRate, actualChannelCount, actualFramesPerBurst);
    
    // Update config with actual values
    if (actualSampleRate != m_config.sampleRate) {
        LOGD("Sample rate adjusted from %d to %d", m_config.sampleRate, actualSampleRate);
        m_config.sampleRate = actualSampleRate;
    }
    
    if (actualFramesPerBurst != m_config.framesPerBurst) {
        LOGD("Frames per burst adjusted from %d to %d", m_config.framesPerBurst, actualFramesPerBurst);
        m_config.framesPerBurst = actualFramesPerBurst;
        m_bufferSize = actualFramesPerBurst * m_config.channelCount;
    }
    
    return EngineResult::SUCCESS;
}

// ═══════════════════════════════════════════════════════════════════════════════════
// PLAYBACK CONTROL
// ═══════════════════════════════════════════════════════════════════════════════════

EngineResult FTLAudioEngine::startPlayback() {
    if (m_engineState.load() != EngineState::INITIALIZED && 
        m_engineState.load() != EngineState::PAUSED) {
        LOGE("Engine not ready for playback");
        return EngineResult::ERROR_NOT_INITIALIZED;
    }
    
    if (!m_audioStream) {
        LOGE("Audio stream not available");
        return EngineResult::ERROR_NOT_INITIALIZED;
    }
    
    m_engineState = EngineState::STARTING;
    
    aaudio_result_t result = AAudioStream_requestStart(m_audioStream);
    if (result != AAUDIO_OK) {
        LOGE("Failed to start audio stream: %s", AAudio_convertResultToText(result));
        m_engineState = EngineState::ERROR;
        return EngineResult::ERROR_PROCESSING_FAILED;
    }
    
    // Wait for stream to start
    aaudio_stream_state_t currentState = AAUDIO_STREAM_STATE_STARTING;
    aaudio_stream_state_t nextState = AAUDIO_STREAM_STATE_UNINITIALIZED;
    
    result = AAudioStream_waitForStateChange(m_audioStream, currentState, &nextState, 1000 * 1000 * 1000); // 1 second timeout
    
    if (result == AAUDIO_OK && nextState == AAUDIO_STREAM_STATE_STARTED) {
        m_engineState = EngineState::RUNNING;
        m_currentMetrics.callbackCount = 0;
        m_currentMetrics.missedCallbacks = 0;
        LOGI("Audio playback started successfully");
        return EngineResult::SUCCESS;
    } else {
        LOGE("Failed to start playback, state: %s", AAudio_convertStreamStateToText(nextState));
        m_engineState = EngineState::ERROR;
        return EngineResult::ERROR_PROCESSING_FAILED;
    }
}

EngineResult FTLAudioEngine::stopPlayback() {
    if (m_engineState.load() != EngineState::RUNNING && 
        m_engineState.load() != EngineState::PAUSED) {
        LOGD("Engine not running, no need to stop");
        return EngineResult::SUCCESS;
    }
    
    if (!m_audioStream) {
        return EngineResult::ERROR_NOT_INITIALIZED;
    }
    
    m_engineState = EngineState::STOPPING;
    
    aaudio_result_t result = AAudioStream_requestStop(m_audioStream);
    if (result != AAUDIO_OK) {
        LOGE("Failed to stop audio stream: %s", AAudio_convertResultToText(result));
        return EngineResult::ERROR_PROCESSING_FAILED;
    }
    
    m_engineState = EngineState::INITIALIZED;
    LOGI("Audio playback stopped");
    return EngineResult::SUCCESS;
}

EngineResult FTLAudioEngine::pausePlayback() {
    if (m_engineState.load() != EngineState::RUNNING) {
        return EngineResult::ERROR_NOT_INITIALIZED;
    }
    
    if (!m_audioStream) {
        return EngineResult::ERROR_NOT_INITIALIZED;
    }
    
    aaudio_result_t result = AAudioStream_requestPause(m_audioStream);
    if (result != AAUDIO_OK) {
        LOGE("Failed to pause audio stream: %s", AAudio_convertResultToText(result));
        return EngineResult::ERROR_PROCESSING_FAILED;
    }
    
    m_engineState = EngineState::PAUSED;
    LOGI("Audio playback paused");
    return EngineResult::SUCCESS;
}

EngineResult FTLAudioEngine::resumePlayback() {
    return startPlayback(); // AAudio doesn't distinguish between start and resume
}

// ═══════════════════════════════════════════════════════════════════════════════════
// AUDIO PROCESSING CALLBACK
// ═══════════════════════════════════════════════════════════════════════════════════

aaudio_data_callback_result_t FTLAudioEngine::audioCallback(
    AAudioStream* stream,
    void* userData,
    void* audioData,
    int32_t numFrames
) {
    auto* engine = static_cast<FTLAudioEngine*>(userData);
    float* outputBuffer = static_cast<float*>(audioData);
    
    // Performance timing start
    auto callbackStart = std::chrono::high_resolution_clock::now();
    
    // Process audio (for now, generate silence or simple test tone)
    engine->processAudioCallback(outputBuffer, numFrames);
    
    // Performance timing end
    auto callbackEnd = std::chrono::high_resolution_clock::now();
    auto processingTime = std::chrono::duration_cast<std::chrono::microseconds>(
        callbackEnd - callbackStart
    ).count();
    
    // Update performance metrics
    engine->updateCallbackMetrics(processingTime);
    
    return AAUDIO_CALLBACK_RESULT_CONTINUE;
}

void FTLAudioEngine::processAudioCallback(float* outputBuffer, int32_t numFrames) {
    // For now, generate a simple test tone or silence
    // This will be replaced with actual audio processing
    
    int totalSamples = numFrames * m_config.channelCount;
    
    if (m_config.enableDSPProcessing) {
        // Generate a quiet test tone at 440Hz for verification
        static double phase = 0.0;
        double phaseIncrement = 2.0 * M_PI * 440.0 / m_config.sampleRate;
        
        for (int i = 0; i < numFrames; ++i) {
            float sample = 0.1f * sin(phase); // Quiet 440Hz tone
            for (int ch = 0; ch < m_config.channelCount; ++ch) {
                outputBuffer[i * m_config.channelCount + ch] = sample;
            }
            phase += phaseIncrement;
            if (phase > 2.0 * M_PI) phase -= 2.0 * M_PI;
        }
    } else {
        // Generate silence
        std::fill(outputBuffer, outputBuffer + totalSamples, 0.0f);
    }
}

void FTLAudioEngine::updateCallbackMetrics(double processingTimeUs) {
    std::lock_guard<std::mutex> lock(m_metricsMutex);
    
    m_currentMetrics.callbackCount++;
    m_currentMetrics.averageProcessingTimeUs = 
        (m_currentMetrics.averageProcessingTimeUs * (m_currentMetrics.callbackCount - 1) + processingTimeUs) / 
        m_currentMetrics.callbackCount;
    
    if (processingTimeUs > m_currentMetrics.maxProcessingTimeUs) {
        m_currentMetrics.maxProcessingTimeUs = processingTimeUs;
    }
    
    // Calculate callback load (percentage of available time used)
    double availableTimeUs = (1000000.0 * m_config.framesPerBurst) / m_config.sampleRate;
    m_currentMetrics.callbackLoad = (processingTimeUs / availableTimeUs) * 100.0;
    
    // Check for potential underruns
    if (m_currentMetrics.callbackLoad > 80.0) { // More than 80% of available time used
        m_currentMetrics.bufferUnderruns++;
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════
// AUDIO BUFFER PROCESSING
// ═══════════════════════════════════════════════════════════════════════════════════

EngineResult FTLAudioEngine::processAudioBuffer(
    const float* inputBuffer,
    float* outputBuffer,
    int bufferSize,
    int sampleRate,
    int channelCount
) {
    if (m_engineState.load() != EngineState::RUNNING && 
        m_engineState.load() != EngineState::INITIALIZED) {
        return EngineResult::ERROR_NOT_INITIALIZED;
    }
    
    // For now, implement pass-through processing
    if (inputBuffer && outputBuffer && bufferSize > 0) {
        std::memcpy(outputBuffer, inputBuffer, bufferSize * sizeof(float));
        return EngineResult::SUCCESS;
    }
    
    return EngineResult::ERROR_PROCESSING_FAILED;
}

// ═══════════════════════════════════════════════════════════════════════════════════
// LATENCY MEASUREMENT
// ═══════════════════════════════════════════════════════════════════════════════════

double FTLAudioEngine::measureLatency() {
    if (!m_audioStream) {
        return -1.0;
    }
    
    // Get AAudio latency estimate
    int64_t framePosition = 0;
    int64_t timeNs = 0;
    
    aaudio_result_t result = AAudioStream_getTimestamp(m_audioStream, CLOCK_MONOTONIC, 
                                                      &framePosition, &timeNs);
    
    if (result == AAUDIO_OK) {
        // Calculate latency based on buffer sizes and frame position
        int32_t bufferSize = AAudioStream_getBufferSizeInFrames(m_audioStream);
        int32_t framesPerBurst = AAudioStream_getFramesPerBurst(m_audioStream);
        
        // Estimate total latency
        double bufferLatencyMs = (double)(bufferSize + framesPerBurst) * 1000.0 / m_config.sampleRate;
        
        std::lock_guard<std::mutex> lock(m_metricsMutex);
        m_currentMetrics.totalLatencyMs = bufferLatencyMs;
        m_currentMetrics.outputLatencyMs = bufferLatencyMs * 0.8; // Estimate
        m_currentMetrics.inputLatencyMs = bufferLatencyMs * 0.2;  // Estimate
        
        LOGD("Measured latency: %.2f ms (target: %.2f ms)", 
             bufferLatencyMs, m_config.targetLatencyMs);
        
        return bufferLatencyMs;
    }
    
    return -1.0;
}

// ═══════════════════════════════════════════════════════════════════════════════════
// PERFORMANCE METRICS
// ═══════════════════════════════════════════════════════════════════════════════════

PerformanceMetrics FTLAudioEngine::getPerformanceMetrics() const {
    std::lock_guard<std::mutex> lock(m_metricsMutex);
    return m_currentMetrics;
}

// ═══════════════════════════════════════════════════════════════════════════════════
// CONFIGURATION
// ═══════════════════════════════════════════════════════════════════════════════════

EngineResult FTLAudioEngine::updateConfiguration(const AudioEngineConfig& config) {
    // For now, only allow updates when engine is not running
    if (m_engineState.load() == EngineState::RUNNING) {
        return EngineResult::ERROR_ALREADY_RUNNING;
    }
    
    auto result = validateConfiguration(config);
    if (result == EngineResult::SUCCESS) {
        m_config = config;
        LOGI("Configuration updated successfully");
    }
    
    return result;
}

AudioEngineConfig FTLAudioEngine::getCurrentConfiguration() const {
    return m_config;
}

EngineState FTLAudioEngine::getCurrentState() const {
    return m_engineState.load();
}

// ═══════════════════════════════════════════════════════════════════════════════════
// CLEANUP
// ═══════════════════════════════════════════════════════════════════════════════════

void FTLAudioEngine::shutdown() {
    if (m_engineState.load() == EngineState::UNINITIALIZED) {
        return;
    }
    
    // Stop playback if running
    if (m_engineState.load() == EngineState::RUNNING || 
        m_engineState.load() == EngineState::PAUSED) {
        stopPlayback();
    }
    
    // Clean up AAudio stream
    cleanupAAudioStream();
    
    // Reset state
    m_engineState = EngineState::UNINITIALIZED;
    
    LOGI("FTL Audio Engine shutdown complete");
}

void FTLAudioEngine::cleanupAAudioStream() {
    if (m_audioStream) {
        AAudioStream_close(m_audioStream);
        m_audioStream = nullptr;
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════
// VALIDATION AND UTILITIES
// ═══════════════════════════════════════════════════════════════════════════════════

EngineResult FTLAudioEngine::validateConfiguration(const AudioEngineConfig& config) const {
    // Validate sample rate
    if (config.sampleRate < 8000 || config.sampleRate > 768000) {
        LOGE("Invalid sample rate: %d", config.sampleRate);
        return EngineResult::ERROR_INVALID_CONFIG;
    }
    
    // Validate channel count
    if (config.channelCount < 1 || config.channelCount > 8) {
        LOGE("Invalid channel count: %d", config.channelCount);
        return EngineResult::ERROR_INVALID_CONFIG;
    }
    
    // Validate buffer size
    if (config.framesPerBurst < 64 || config.framesPerBurst > 2048) {
        LOGE("Invalid frames per burst: %d", config.framesPerBurst);
        return EngineResult::ERROR_INVALID_CONFIG;
    }
    
    // Validate target latency
    if (config.targetLatencyMs < 1.0 || config.targetLatencyMs > 1000.0) {
        LOGE("Invalid target latency: %.2f ms", config.targetLatencyMs);
        return EngineResult::ERROR_INVALID_CONFIG;
    }
    
    return EngineResult::SUCCESS;
}

void FTLAudioEngine::logConfiguration(const AudioEngineConfig& config) const {
    LOGI("Audio Engine Configuration:");
    LOGI("  Sample Rate: %d Hz", config.sampleRate);
    LOGI("  Frames per Burst: %d", config.framesPerBurst);
    LOGI("  Channel Count: %d", config.channelCount);
    LOGI("  Target Latency: %.2f ms", config.targetLatencyMs);
    LOGI("  Low Latency Mode: %s", config.enableLowLatency ? "enabled" : "disabled");
    LOGI("  DSP Processing: %s", config.enableDSPProcessing ? "enabled" : "disabled");
}

// ═══════════════════════════════════════════════════════════════════════════════════
// ERROR CALLBACK
// ═══════════════════════════════════════════════════════════════════════════════════

void FTLAudioEngine::errorCallback(AAudioStream* stream, void* userData, aaudio_result_t error) {
    auto* engine = static_cast<FTLAudioEngine*>(userData);
    LOGE("AAudio error callback: %s", AAudio_convertResultToText(error));
    
    // Handle error - for now just log and set error state
    engine->m_engineState = EngineState::ERROR;
    
    // Could implement recovery logic here
}

} // namespace ftl_audio