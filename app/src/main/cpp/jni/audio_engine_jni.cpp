/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              FTL AUDIO ENGINE - JNI BRIDGE                  ║
 * ║           Neural-Enhanced C++ to Kotlin Interface           ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
 *
 * Performance Targets:
 * • JNI Call Overhead: <100µs per call
 * • Audio Buffer Transfer: <1ms zero-copy when possible
 * • Memory Allocation: Minimal heap allocations in audio thread
 * • Thread Safety: Lock-free audio processing path
 */

#include <jni.h>
#include <android/log.h>
#include <memory>
#include <unordered_map>
#include <mutex>

#include "../audio_engine/FTLAudioEngine.h"
#include "jni_helpers.h"

// ═══════════════════════════════════════════════════════════════════════════════════
// CONSTANTS & MACROS
// ═══════════════════════════════════════════════════════════════════════════════════

#define LOG_TAG "FTL_Audio_JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// Namespace for all FTL audio functions
namespace ftl_audio {

// ═══════════════════════════════════════════════════════════════════════════════════
// GLOBAL STATE MANAGEMENT
// ═══════════════════════════════════════════════════════════════════════════════════

// Thread-safe container for active audio engines
static std::unordered_map<jlong, std::unique_ptr<FTLAudioEngine>> g_engineMap;
static std::mutex g_engineMapMutex;

// Helper to get engine by handle with thread safety
FTLAudioEngine* getEngineByHandle(jlong handle) {
    std::lock_guard<std::mutex> lock(g_engineMapMutex);
    auto it = g_engineMap.find(handle);
    return (it != g_engineMap.end()) ? it->second.get() : nullptr;
}

// Generate unique handle for new engine
jlong generateEngineHandle() {
    static std::atomic<jlong> handleCounter{1000}; // Start from 1000 to avoid confusion with error codes
    return handleCounter.fetch_add(1);
}

} // namespace ftl_audio

// ═══════════════════════════════════════════════════════════════════════════════════
// JNI IMPLEMENTATIONS
// ═══════════════════════════════════════════════════════════════════════════════════

extern "C" {

/**
 * Initialize native audio engine
 * 
 * Java signature: 
 * nativeInitializeEngine(sampleRate: Int, framesPerBurst: Int, channelCount: Int, format: Int, deviceId: Int): Long
 */
JNIEXPORT jlong JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_nativeInitializeEngine(
    JNIEnv *env, 
    jobject /* this */,
    jint sampleRate,
    jint framesPerBurst, 
    jint channelCount,
    jint format,
    jint deviceId
) {
    LOGI("Initializing FTL Audio Engine: SR=%d, Frames=%d, Channels=%d", 
         sampleRate, framesPerBurst, channelCount);
    
    try {
        // Create new audio engine instance
        auto engine = std::make_unique<ftl_audio::FTLAudioEngine>();
        
        // Configure engine parameters
        ftl_audio::AudioEngineConfig config;
        config.sampleRate = sampleRate;
        config.framesPerBurst = framesPerBurst;
        config.channelCount = channelCount;
        config.audioFormat = static_cast<ftl_audio::AudioFormat>(format);
        config.deviceId = deviceId;
        config.enableLowLatency = true;
        config.targetLatencyMs = 10.0; // <10ms target
        
        // Initialize the engine
        auto result = engine->initialize(config);
        if (result != ftl_audio::EngineResult::SUCCESS) {
            LOGE("Failed to initialize audio engine: %d", static_cast<int>(result));
            return static_cast<jlong>(result); // Return negative error code
        }
        
        // Generate handle and store engine
        jlong handle = ftl_audio::generateEngineHandle();
        {
            std::lock_guard<std::mutex> lock(ftl_audio::g_engineMapMutex);
            ftl_audio::g_engineMap[handle] = std::move(engine);
        }
        
        LOGI("Audio engine initialized successfully with handle: %lld", handle);
        return handle;
        
    } catch (const std::exception& e) {
        LOGE("Exception during engine initialization: %s", e.what());
        return -1; // Generic error
    }
}

/**
 * Start playback on native engine
 */
JNIEXPORT jboolean JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_nativeStartPlayback(
    JNIEnv *env, 
    jobject /* this */,
    jlong engineHandle
) {
    auto* engine = ftl_audio::getEngineByHandle(engineHandle);
    if (!engine) {
        LOGE("Invalid engine handle for start playback: %lld", engineHandle);
        return JNI_FALSE;
    }
    
    auto result = engine->startPlayback();
    if (result == ftl_audio::EngineResult::SUCCESS) {
        LOGI("Playback started successfully");
        return JNI_TRUE;
    } else {
        LOGE("Failed to start playback: %d", static_cast<int>(result));
        return JNI_FALSE;
    }
}

/**
 * Stop playback on native engine
 */
JNIEXPORT jboolean JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_nativeStopPlayback(
    JNIEnv *env, 
    jobject /* this */,
    jlong engineHandle
) {
    auto* engine = ftl_audio::getEngineByHandle(engineHandle);
    if (!engine) {
        LOGE("Invalid engine handle for stop playback: %lld", engineHandle);
        return JNI_FALSE;
    }
    
    auto result = engine->stopPlayback();
    return (result == ftl_audio::EngineResult::SUCCESS) ? JNI_TRUE : JNI_FALSE;
}

/**
 * Pause playback on native engine
 */
JNIEXPORT jboolean JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_nativePausePlayback(
    JNIEnv *env, 
    jobject /* this */,
    jlong engineHandle
) {
    auto* engine = ftl_audio::getEngineByHandle(engineHandle);
    if (!engine) {
        LOGE("Invalid engine handle for pause playback: %lld", engineHandle);
        return JNI_FALSE;
    }
    
    auto result = engine->pausePlayback();
    return (result == ftl_audio::EngineResult::SUCCESS) ? JNI_TRUE : JNI_FALSE;
}

/**
 * Resume playback on native engine
 */
JNIEXPORT jboolean JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_nativeResumePlayback(
    JNIEnv *env, 
    jobject /* this */,
    jlong engineHandle
) {
    auto* engine = ftl_audio::getEngineByHandle(engineHandle);
    if (!engine) {
        LOGE("Invalid engine handle for resume playback: %lld", engineHandle);
        return JNI_FALSE;
    }
    
    auto result = engine->resumePlayback();
    return (result == ftl_audio::EngineResult::SUCCESS) ? JNI_TRUE : JNI_FALSE;
}

/**
 * Process audio buffer through native engine
 * This is the most performance-critical function - must be optimized for <1ms execution
 */
JNIEXPORT jfloatArray JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_nativeProcessAudioBuffer(
    JNIEnv *env, 
    jobject /* this */,
    jlong engineHandle,
    jfloatArray inputBuffer,
    jint bufferSize,
    jint sampleRate,
    jint channelCount
) {
    auto* engine = ftl_audio::getEngineByHandle(engineHandle);
    if (!engine) {
        LOGE("Invalid engine handle for process audio buffer: %lld", engineHandle);
        return nullptr;
    }
    
    // Get input buffer - using critical section for performance
    jfloat* inputData = env->GetFloatArrayElements(inputBuffer, nullptr);
    if (!inputData) {
        LOGE("Failed to get input buffer elements");
        return nullptr;
    }
    
    // Prepare output buffer
    jfloatArray outputArray = env->NewFloatArray(bufferSize);
    if (!outputArray) {
        env->ReleaseFloatArrayElements(inputBuffer, inputData, JNI_ABORT);
        LOGE("Failed to create output array");
        return nullptr;
    }
    
    jfloat* outputData = env->GetFloatArrayElements(outputArray, nullptr);
    if (!outputData) {
        env->ReleaseFloatArrayElements(inputBuffer, inputData, JNI_ABORT);
        LOGE("Failed to get output buffer elements");
        return nullptr;
    }
    
    // Process audio through engine (this must be <1ms for real-time performance)
    auto result = engine->processAudioBuffer(
        inputData, 
        outputData, 
        bufferSize,
        sampleRate,
        channelCount
    );
    
    // Release input buffer (no need to copy back)
    env->ReleaseFloatArrayElements(inputBuffer, inputData, JNI_ABORT);
    
    if (result == ftl_audio::EngineResult::SUCCESS) {
        // Release output buffer and commit changes
        env->ReleaseFloatArrayElements(outputArray, outputData, 0);
        return outputArray;
    } else {
        // Release output buffer without committing
        env->ReleaseFloatArrayElements(outputArray, outputData, JNI_ABORT);
        LOGE("Audio processing failed: %d", static_cast<int>(result));
        return nullptr;
    }
}

/**
 * Measure audio latency in native engine
 */
JNIEXPORT jdouble JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_nativeMeasureLatency(
    JNIEnv *env, 
    jobject /* this */,
    jlong engineHandle
) {
    auto* engine = ftl_audio::getEngineByHandle(engineHandle);
    if (!engine) {
        LOGE("Invalid engine handle for measure latency: %lld", engineHandle);
        return -1.0;
    }
    
    double latencyMs = engine->measureLatency();
    LOGI("Measured audio latency: %.2f ms", latencyMs);
    return latencyMs;
}

/**
 * Get performance metrics from native engine
 * Returns a PerformanceMetrics object to Kotlin
 */
JNIEXPORT jobject JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_nativeGetPerformanceMetrics(
    JNIEnv *env, 
    jobject /* this */,
    jlong engineHandle
) {
    auto* engine = ftl_audio::getEngineByHandle(engineHandle);
    if (!engine) {
        LOGE("Invalid engine handle for get performance metrics: %lld", engineHandle);
        return nullptr;
    }
    
    auto metrics = engine->getPerformanceMetrics();
    
    // Create PerformanceMetrics object in Kotlin
    return ftl_audio::createPerformanceMetricsObject(env, metrics);
}

/**
 * Update native engine configuration
 */
JNIEXPORT jboolean JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_nativeUpdateConfiguration(
    JNIEnv *env, 
    jobject /* this */,
    jlong engineHandle,
    jobject configObject
) {
    auto* engine = ftl_audio::getEngineByHandle(engineHandle);
    if (!engine) {
        LOGE("Invalid engine handle for update configuration: %lld", engineHandle);
        return JNI_FALSE;
    }
    
    // Convert Kotlin configuration object to C++ config
    auto config = ftl_audio::extractAudioEngineConfiguration(env, configObject);
    
    auto result = engine->updateConfiguration(config);
    return (result == ftl_audio::EngineResult::SUCCESS) ? JNI_TRUE : JNI_FALSE;
}

/**
 * Shutdown native audio engine and clean up resources
 */
JNIEXPORT void JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_nativeShutdownEngine(
    JNIEnv *env, 
    jobject /* this */,
    jlong engineHandle
) {
    LOGI("Shutting down audio engine with handle: %lld", engineHandle);
    
    {
        std::lock_guard<std::mutex> lock(ftl_audio::g_engineMapMutex);
        auto it = ftl_audio::g_engineMap.find(engineHandle);
        if (it != ftl_audio::g_engineMap.end()) {
            // Engine destructor will handle cleanup
            ftl_audio::g_engineMap.erase(it);
            LOGI("Audio engine shutdown complete");
        } else {
            LOGE("Invalid engine handle for shutdown: %lld", engineHandle);
        }
    }
}

} // extern "C"