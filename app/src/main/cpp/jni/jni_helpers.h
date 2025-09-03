/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║               FTL AUDIO ENGINE - JNI HELPERS                ║
 * ║            Utility Functions for JNI Integration            ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
 */

#ifndef FTL_JNI_HELPERS_H
#define FTL_JNI_HELPERS_H

#include <jni.h>
#include <unordered_map>
#include <mutex>
#include "../audio_engine/FTLAudioEngine.h"

namespace ftl_audio {

/**
 * Create a Kotlin PerformanceMetrics object from C++ data
 */
jobject createPerformanceMetricsObject(JNIEnv* env, const PerformanceMetrics& metrics);

/**
 * Extract AudioEngineConfiguration from Kotlin object
 */
AudioEngineConfig extractAudioEngineConfiguration(JNIEnv* env, jobject configObject);

/**
 * Helper to safely get field IDs with caching
 */
class FieldCache {
public:
    static jfieldID getFieldID(JNIEnv* env, jclass clazz, const char* name, const char* sig);
private:
    static std::unordered_map<std::string, jfieldID> fieldCache;
    static std::mutex cacheMutex;
};

/**
 * Exception handling utilities
 */
void throwJavaException(JNIEnv* env, const char* className, const char* message);
bool checkAndClearException(JNIEnv* env);

} // namespace ftl_audio

#endif // FTL_JNI_HELPERS_H