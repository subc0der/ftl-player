#include <jni.h>
#include <android/log.h>

#define LOG_TAG "FTLAudioEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" {

JNIEXPORT jstring JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_getVersion(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("FTL Audio Engine v1.0.0");
}

JNIEXPORT jboolean JNICALL
Java_com_ftl_audioplayer_audio_AudioEngine_initialize(JNIEnv *env, jclass clazz) {
    LOGI("FTL Audio Engine initialized");
    return JNI_TRUE;
}

}