# ╔══════════════════════════════════════════════════════════════╗
# ║            FTL AUDIO ENGINE - CMAKE BUILD SYSTEM            ║
# ║              Neural-Enhanced C++ Audio Processing           ║
# ╚══════════════════════════════════════════════════════════════╝
#
# 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
#
# Performance Targets:
# • Audio Latency: <10ms total pipeline
# • THD+N: <0.001% @ 1kHz, -60dB
# • CPU Usage: <15% normal operation
# • Memory Usage: <50MB per audio stream

cmake_minimum_required(VERSION 3.22.1)
project(ftl_audio_engine)

# ═══════════════════════════════════════════════════════════════════════════════════
# COMPILER FLAGS & OPTIMIZATION
# ═══════════════════════════════════════════════════════════════════════════════════

# C++ standard and optimization flags
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Performance optimization flags
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O3 -ffast-math -funroll-loops")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -fomit-frame-pointer -finline-functions")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -DNDEBUG -DTARGET_LATENCY_MS=10")

# ARM-specific optimizations
if(ANDROID_ABI STREQUAL "arm64-v8a")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -march=armv8-a -mtune=cortex-a75")
elseif(ANDROID_ABI STREQUAL "armeabi-v7a")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -march=armv7-a -mfpu=neon -mtune=cortex-a15")
endif()

# Debug flags for development
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -g -O0 -DDEBUG_AUDIO_ENGINE")

# ═══════════════════════════════════════════════════════════════════════════════════
# ANDROID NDK INTEGRATION
# ═══════════════════════════════════════════════════════════════════════════════════

# Find required Android libraries
find_library(log-lib log)
find_library(android-lib android)

# Audio libraries (AAudio for Android 8.0+, OpenSL ES for compatibility)
find_library(aaudio-lib aaudio)
find_library(opensles-lib OpenSLES)

# ═══════════════════════════════════════════════════════════════════════════════════
# INCLUDE DIRECTORIES
# ═══════════════════════════════════════════════════════════════════════════════════

include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}/audio_engine
    ${CMAKE_CURRENT_SOURCE_DIR}/dsp
    ${CMAKE_CURRENT_SOURCE_DIR}/utils
)

# ═══════════════════════════════════════════════════════════════════════════════════
# SOURCE FILES
# ═══════════════════════════════════════════════════════════════════════════════════

# JNI interface layer
set(JNI_SOURCES
    jni/audio_engine_jni.cpp
    jni/jni_helpers.cpp
)

# Core audio engine
set(AUDIO_ENGINE_SOURCES
    audio_engine/FTLAudioEngine.cpp
    audio_engine/AudioRenderer.cpp
    audio_engine/AudioStream.cpp
    audio_engine/LatencyMonitor.cpp
    audio_engine/PerformanceMonitor.cpp
)

# DSP processing modules
set(DSP_SOURCES
    dsp/AudioProcessor.cpp
    dsp/BufferManager.cpp
    dsp/RealtimeProcessor.cpp
    dsp/AudioFormat.cpp
)

# Utility modules
set(UTILITY_SOURCES
    utils/ThreadUtils.cpp
    utils/TimeUtils.cpp
    utils/LogUtils.cpp
    utils/MathUtils.cpp
)

# ═══════════════════════════════════════════════════════════════════════════════════
# LIBRARY TARGET
# ═══════════════════════════════════════════════════════════════════════════════════

add_library(
    ftl_audio_engine
    SHARED
    ${JNI_SOURCES}
    ${AUDIO_ENGINE_SOURCES}
    ${DSP_SOURCES}
    ${UTILITY_SOURCES}
)

# ═══════════════════════════════════════════════════════════════════════════════════
# LINKED LIBRARIES
# ═══════════════════════════════════════════════════════════════════════════════════

target_link_libraries(
    ftl_audio_engine
    ${log-lib}
    ${android-lib}
    ${aaudio-lib}
    ${opensles-lib}
    atomic
)

# ═══════════════════════════════════════════════════════════════════════════════════
# COMPILER-SPECIFIC SETTINGS
# ═══════════════════════════════════════════════════════════════════════════════════

# Enable NEON SIMD for ARM processors
if(ANDROID_ABI STREQUAL "arm64-v8a" OR ANDROID_ABI STREQUAL "armeabi-v7a")
    target_compile_definitions(ftl_audio_engine PRIVATE ENABLE_NEON_SIMD=1)
endif()

# Threading and real-time processing
target_compile_definitions(ftl_audio_engine PRIVATE 
    USE_REALTIME_THREADS=1
    TARGET_LATENCY_MS=10
    MAX_AUDIO_CHANNELS=8
    DEFAULT_SAMPLE_RATE=48000
)

# Build configuration specific definitions
if(CMAKE_BUILD_TYPE STREQUAL "Debug")
    target_compile_definitions(ftl_audio_engine PRIVATE DEBUG_BUILD=1)
else()
    target_compile_definitions(ftl_audio_engine PRIVATE RELEASE_BUILD=1)
endif()

# ═══════════════════════════════════════════════════════════════════════════════════
# INSTALL AND PACKAGING
# ═══════════════════════════════════════════════════════════════════════════════════

# Strip symbols in release builds for smaller binary size
if(CMAKE_BUILD_TYPE STREQUAL "Release")
    set_target_properties(ftl_audio_engine PROPERTIES LINK_FLAGS_RELEASE -s)
endif()