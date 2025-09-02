/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║               FTL AUDIO ENGINE - JNI HELPERS                ║
 * ║            Utility Functions for JNI Integration            ║
 * ╚══════════════════════════════════════════════════════════════╝
 */

#include "jni_helpers.h"
#include <android/log.h>
#include <unordered_map>
#include <mutex>

#define LOG_TAG "FTL_JNI_Helpers"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace ftl_audio {

// JNI Method Signatures
namespace JNISignatures {
    // D = double, J = long, V = void
    // Constructor signature: cpuUsage, memoryUsage, bufferUnderruns, bufferOverruns, 
    //                       avgProcessingTime, maxProcessingTime, callbackCount, missedCallbacks, callbackLoad
    static const char* PERFORMANCE_METRICS_CONSTRUCTOR = "(DDJJDDJJD)V";
}

// Static field cache for performance
std::unordered_map<std::string, jfieldID> FieldCache::fieldCache;
std::mutex FieldCache::cacheMutex;

// ═══════════════════════════════════════════════════════════════════════════════════
// PERFORMANCE METRICS OBJECT CREATION
// ═══════════════════════════════════════════════════════════════════════════════════

jobject createPerformanceMetricsObject(JNIEnv* env, const PerformanceMetrics& metrics) {
    // Find PerformanceMetrics class
    jclass metricsClass = env->FindClass("com/ftl/audioplayer/audio/PerformanceMetrics");
    if (!metricsClass) {
        LOGE("Could not find PerformanceMetrics class");
        return nullptr;
    }
    
    // Get constructor method ID (updated signature for new fields)
    jmethodID constructor = env->GetMethodID(metricsClass, "<init>", JNISignatures::PERFORMANCE_METRICS_CONSTRUCTOR);
    if (!constructor) {
        LOGE("Could not find PerformanceMetrics constructor");
        env->DeleteLocalRef(metricsClass);
        return nullptr;
    }
    
    // Create new instance
    jobject metricsObject = env->NewObject(
        metricsClass, 
        constructor,
        metrics.cpuUsagePercent,
        metrics.memoryUsageMB,
        static_cast<jlong>(metrics.bufferUnderruns),
        static_cast<jlong>(metrics.bufferOverruns),
        metrics.averageProcessingTimeUs,
        metrics.maxProcessingTimeUs,
        static_cast<jlong>(metrics.callbackCount),
        static_cast<jlong>(metrics.missedCallbacks),
        metrics.callbackLoad
    );
    
    env->DeleteLocalRef(metricsClass);
    return metricsObject;
}

// ═══════════════════════════════════════════════════════════════════════════════════
// CONFIGURATION OBJECT EXTRACTION
// ═══════════════════════════════════════════════════════════════════════════════════

AudioEngineConfig extractAudioEngineConfiguration(JNIEnv* env, jobject configObject) {
    AudioEngineConfig config;
    
    if (!configObject) {
        LOGE("Configuration object is null");
        return config;
    }
    
    jclass configClass = env->GetObjectClass(configObject);
    if (!configClass) {
        LOGE("Could not get configuration class");
        return config;
    }
    
    // Extract boolean fields
    jfieldID enableLowLatencyField = FieldCache::getFieldID(env, configClass, "enableLowLatencyMode", "Z");
    if (enableLowLatencyField) {
        config.enableLowLatency = env->GetBooleanField(configObject, enableLowLatencyField);
    }
    
    jfieldID enableHighResField = FieldCache::getFieldID(env, configClass, "enableHighResolution", "Z");
    if (enableHighResField) {
        config.enableHighResolution = env->GetBooleanField(configObject, enableHighResField);
    }
    
    jfieldID enableDSPField = FieldCache::getFieldID(env, configClass, "enableDSPProcessing", "Z");
    if (enableDSPField) {
        config.enableDSPProcessing = env->GetBooleanField(configObject, enableDSPField);
    }
    
    // Extract float fields
    jfieldID bufferMultiplierField = FieldCache::getFieldID(env, configClass, "bufferSizeMultiplier", "F");
    if (bufferMultiplierField) {
        config.bufferSizeMultiplier = env->GetFloatField(configObject, bufferMultiplierField);
    }
    
    // Extract int fields
    jfieldID threadPriorityField = FieldCache::getFieldID(env, configClass, "threadPriority", "I");
    if (threadPriorityField) {
        config.threadPriority = env->GetIntField(configObject, threadPriorityField);
    }
    
    env->DeleteLocalRef(configClass);
    return config;
}

// ═══════════════════════════════════════════════════════════════════════════════════
// FIELD CACHE IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════════

jfieldID FieldCache::getFieldID(JNIEnv* env, jclass clazz, const char* name, const char* sig) {
    std::string key = std::string(name) + ":" + std::string(sig);
    
    {
        std::lock_guard<std::mutex> lock(cacheMutex);
        auto it = fieldCache.find(key);
        if (it != fieldCache.end()) {
            return it->second;
        }
    }
    
    jfieldID fieldID = env->GetFieldID(clazz, name, sig);
    if (fieldID) {
        std::lock_guard<std::mutex> lock(cacheMutex);
        fieldCache[key] = fieldID;
    } else {
        LOGE("Could not find field: %s with signature: %s", name, sig);
    }
    
    return fieldID;
}

// ═══════════════════════════════════════════════════════════════════════════════════
// EXCEPTION HANDLING
// ═══════════════════════════════════════════════════════════════════════════════════

void throwJavaException(JNIEnv* env, const char* className, const char* message) {
    jclass exceptionClass = env->FindClass(className);
    if (exceptionClass) {
        env->ThrowNew(exceptionClass, message);
        env->DeleteLocalRef(exceptionClass);
    } else {
        LOGE("Could not find exception class: %s", className);
    }
}

bool checkAndClearException(JNIEnv* env) {
    if (env->ExceptionCheck()) {
        jthrowable exception = env->ExceptionOccurred();
        if (exception) {
            env->ExceptionDescribe(); // Print to logcat
            env->ExceptionClear();
            env->DeleteLocalRef(exception);
            return true;
        }
    }
    return false;
}

} // namespace ftl_audio