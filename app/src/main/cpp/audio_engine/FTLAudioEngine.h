/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              FTL AUDIO ENGINE - CORE HEADER                 ║
 * ║           Neural-Enhanced Real-Time Audio Processing        ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵
 *
 * Performance Targets:
 * • Total Latency: <10ms end-to-end
 * • THD+N: <0.001% @ 1kHz, -60dB
 * • CPU Usage: <15% normal operation
 * • Memory Usage: <50MB per audio stream
 */

#ifndef FTL_AUDIO_ENGINE_H
#define FTL_AUDIO_ENGINE_H

#include <memory>
#include <atomic>
#include <thread>
#include <chrono>
#include <aaudio/AAudio.h>

namespace ftl_audio {

// ═══════════════════════════════════════════════════════════════════════════════════
// ENUMS AND CONSTANTS
// ═══════════════════════════════════════════════════════════════════════════════════

enum class EngineResult {
    SUCCESS = 0,
    ERROR_INVALID_CONFIG = -1,
    ERROR_HARDWARE_UNAVAILABLE = -2,
    ERROR_OUT_OF_MEMORY = -3,
    ERROR_ALREADY_RUNNING = -4,
    ERROR_NOT_INITIALIZED = -5,
    ERROR_PROCESSING_FAILED = -6,
    ERROR_LATENCY_TOO_HIGH = -7
};

enum class AudioFormat {
    PCM_16 = 1,
    PCM_24 = 2,
    PCM_FLOAT32 = 3,
    DSD64 = 10,
    DSD128 = 11,
    DSD256 = 12,
    DSD512 = 13
};

enum class EngineState {
    UNINITIALIZED,
    INITIALIZED,
    STARTING,
    RUNNING,
    PAUSED,
    STOPPING,
    ERROR
};

// ═══════════════════════════════════════════════════════════════════════════════════
// CONFIGURATION STRUCTURES
// ═══════════════════════════════════════════════════════════════════════════════════

struct AudioEngineConfig {
    int sampleRate = 48000;
    int framesPerBurst = 256;
    int channelCount = 2;
    AudioFormat audioFormat = AudioFormat::PCM_FLOAT32;
    int deviceId = 0;
    
    // Performance settings
    bool enableLowLatency = true;
    bool enableHighResolution = false;
    bool enableDSPProcessing = true;
    double targetLatencyMs = 10.0;
    
    // Threading
    int threadPriority = -19; // THREAD_PRIORITY_URGENT_AUDIO
    float bufferSizeMultiplier = 1.0f;
    
    // Advanced settings
    bool enableRealTimeCallback = true;
    bool enableExclusiveMode = false;
    int maxBufferSizeFrames = 2048;
    int minBufferSizeFrames = 64;
};

struct PerformanceMetrics {
    double cpuUsagePercent = 0.0;
    double memoryUsageMB = 0.0;
    uint64_t bufferUnderruns = 0;
    uint64_t bufferOverruns = 0;
    double averageProcessingTimeUs = 0.0;
    double maxProcessingTimeUs = 0.0;
    
    // Latency measurements
    double inputLatencyMs = 0.0;
    double outputLatencyMs = 0.0;
    double totalLatencyMs = 0.0;
    
    // Quality metrics
    double thdPlusN = 0.0; // Total Harmonic Distortion + Noise
    double signalToNoiseRatio = 0.0;
    
    // Real-time performance
    uint64_t callbackCount = 0;
    uint64_t missedCallbacks = 0;
    double callbackLoad = 0.0; // Percentage of available time used
};

// ═══════════════════════════════════════════════════════════════════════════════════
// FORWARD DECLARATIONS
// ═══════════════════════════════════════════════════════════════════════════════════

class AudioRenderer;
class LatencyMonitor;
class PerformanceMonitor;
class AudioProcessor;

// ═══════════════════════════════════════════════════════════════════════════════════
// MAIN AUDIO ENGINE CLASS
// ═══════════════════════════════════════════════════════════════════════════════════

class FTLAudioEngine {
public:
    // Constructor/Destructor
    FTLAudioEngine();
    ~FTLAudioEngine();
    
    // Core lifecycle
    EngineResult initialize(const AudioEngineConfig& config);
    EngineResult startPlayback();
    EngineResult stopPlayback();
    EngineResult pausePlayback();
    EngineResult resumePlayback();
    void shutdown();
    
    // Audio processing
    EngineResult processAudioBuffer(
        const float* inputBuffer,
        float* outputBuffer,
        int bufferSize,
        int sampleRate,
        int channelCount
    );
    
    // Configuration
    EngineResult updateConfiguration(const AudioEngineConfig& config);
    AudioEngineConfig getCurrentConfiguration() const;
    
    // Monitoring
    double measureLatency();
    PerformanceMetrics getPerformanceMetrics() const;
    EngineState getCurrentState() const;
    
    // Advanced features
    EngineResult setAudioSource(const std::string& filePath);
    EngineResult enableEffect(const std::string& effectName, bool enable);
    EngineResult setEffectParameter(const std::string& effectName, 
                                   const std::string& paramName, 
                                   float value);

private:
    // Internal state
    std::atomic<EngineState> m_engineState{EngineState::UNINITIALIZED};
    AudioEngineConfig m_config;
    
    // Audio stream components (will be implemented in future iterations)
    // std::unique_ptr<AudioRenderer> m_audioRenderer;
    // std::unique_ptr<LatencyMonitor> m_latencyMonitor;
    // std::unique_ptr<PerformanceMonitor> m_performanceMonitor;
    // std::unique_ptr<AudioProcessor> m_audioProcessor;
    
    // AAudio stream
    AAudioStream* m_audioStream = nullptr;
    
    // Performance tracking
    mutable std::mutex m_metricsMutex;
    PerformanceMetrics m_currentMetrics;
    
    // Threading
    std::thread m_processingThread;
    std::atomic<bool> m_stopProcessing{false};
    
    // Buffer management
    std::unique_ptr<float[]> m_audioBuffer;
    std::atomic<int> m_bufferSize{0};
    
    // Timing
    std::chrono::high_resolution_clock::time_point m_startTime;
    std::chrono::high_resolution_clock::time_point m_lastCallbackTime;
    
    // Internal methods
    EngineResult setupAAudioStream();
    void cleanupAAudioStream();
    void processAudioCallback(float* outputBuffer, int32_t numFrames);
    void updateCallbackMetrics(double processingTimeUs);
    static aaudio_data_callback_result_t audioCallback(
        AAudioStream* stream,
        void* userData,
        void* audioData,
        int32_t numFrames
    );
    static void errorCallback(
        AAudioStream* stream,
        void* userData,
        aaudio_result_t error
    );
    
    void processingThreadFunction();
    void updatePerformanceMetrics();
    EngineResult validateConfiguration(const AudioEngineConfig& config) const;
    
    // Helper methods
    double getCurrentTimeMs() const;
    void logConfiguration(const AudioEngineConfig& config) const;
    
    // Prevent copy and assignment
    FTLAudioEngine(const FTLAudioEngine&) = delete;
    FTLAudioEngine& operator=(const FTLAudioEngine&) = delete;
};

} // namespace ftl_audio

#endif // FTL_AUDIO_ENGINE_H