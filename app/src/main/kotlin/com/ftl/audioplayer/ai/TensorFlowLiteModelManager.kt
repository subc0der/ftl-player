package com.ftl.audioplayer.ai

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              TENSORFLOW LITE MODEL MANAGER                  ║
 * ║        AI Model Loading and Inference Management            ║
 * ╚══════════════════════════════════════════════════════════════╝
 * 
 * Manages TensorFlow Lite model loading, initialization, and inference
 * for neural audio processing features.
 */
@Singleton
class TensorFlowLiteModelManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "TFLiteModelManager"
        
        // Model file names in assets folder
        private const val GENRE_MODEL_FILE = "genre_classification.tflite"
        private const val MOOD_MODEL_FILE = "mood_detection.tflite"
        private const val AUDIO_FEATURES_MODEL_FILE = "audio_features.tflite"
        private const val EQ_SUGGESTION_MODEL_FILE = "eq_optimization.tflite"
        
        // Model input/output specifications
        const val AUDIO_FEATURE_INPUT_SIZE = 128
        const val GENRE_OUTPUT_SIZE = 10  // Number of supported genres
        const val MOOD_OUTPUT_SIZE = 8    // Number of AudioMood enum values
        const val EQ_OUTPUT_SIZE = 32     // Number of EQ bands
    }
    
    // TensorFlow Lite interpreters for each model
    private var genreInterpreter: Interpreter? = null
    private var moodInterpreter: Interpreter? = null
    private var audioFeaturesInterpreter: Interpreter? = null
    private var eqSuggestionInterpreter: Interpreter? = null
    
    // GPU delegate for hardware acceleration
    private var gpuDelegate: GpuDelegate? = null
    
    private var isInitialized = false
    
    /**
     * Initialize all TensorFlow Lite models
     * @throws RuntimeException if model loading fails
     */
    suspend fun initializeModels() {
        if (isInitialized) return
        
        try {
            Log.i(TAG, "Initializing TensorFlow Lite models...")
            
            // Check GPU compatibility and create delegate if available
            val compatList = CompatibilityList()
            val options = Interpreter.Options()
            
            if (compatList.isDelegateSupportedOnThisDevice) {
                gpuDelegate = GpuDelegate()
                options.addDelegate(gpuDelegate)
                Log.i(TAG, "GPU acceleration enabled for TensorFlow Lite")
            } else {
                Log.w(TAG, "GPU acceleration not supported on this device")
            }
            
            // Set thread count for CPU inference
            options.setNumThreads(4)
            
            // Load each model with error handling
            try {
                genreInterpreter = loadModel(GENRE_MODEL_FILE, options)
                Log.d(TAG, "Genre classification model loaded successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load genre classification model: ${e.message}")
            }
            
            try {
                moodInterpreter = loadModel(MOOD_MODEL_FILE, options)
                Log.d(TAG, "Mood detection model loaded successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load mood detection model: ${e.message}")
            }
            
            try {
                audioFeaturesInterpreter = loadModel(AUDIO_FEATURES_MODEL_FILE, options)
                Log.d(TAG, "Audio features model loaded successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load audio features model: ${e.message}")
            }
            
            try {
                eqSuggestionInterpreter = loadModel(EQ_SUGGESTION_MODEL_FILE, options)
                Log.d(TAG, "EQ suggestion model loaded successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load EQ suggestion model: ${e.message}")
            }
            
            isInitialized = true
            Log.i(TAG, "TensorFlow Lite model initialization completed")
            
        } catch (e: Exception) {
            isInitialized = false
            cleanupResources()
            throw RuntimeException("Failed to initialize TensorFlow Lite models: ${e.message}", e)
        }
    }
    
    /**
     * Run genre classification inference
     */
    fun classifyGenre(audioFeatures: FloatArray): Pair<String, Float> {
        return try {
            genreInterpreter?.let { interpreter ->
                val outputArray = Array(1) { FloatArray(GENRE_OUTPUT_SIZE) }
                val inputArray = Array(1) { audioFeatures }
                
                interpreter.run(inputArray, outputArray)
                
                val predictions = outputArray[0]
                val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: 0
                val confidence = predictions[maxIndex]
                
                val genre = getGenreFromIndex(maxIndex)
                genre to confidence
            } ?: ("Unknown" to 0.0f)
        } catch (e: Exception) {
            Log.e(TAG, "Genre classification inference failed: ${e.message}")
            "Unknown" to 0.0f
        }
    }
    
    /**
     * Run mood detection inference
     */
    fun detectMood(audioFeatures: FloatArray): Pair<AudioMood, Float> {
        return try {
            moodInterpreter?.let { interpreter ->
                val outputArray = Array(1) { FloatArray(MOOD_OUTPUT_SIZE) }
                val inputArray = Array(1) { audioFeatures }
                
                interpreter.run(inputArray, outputArray)
                
                val predictions = outputArray[0]
                val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: 0
                val confidence = predictions[maxIndex]
                
                val mood = AudioMood.values()[maxIndex.coerceIn(0, AudioMood.values().size - 1)]
                mood to confidence
            } ?: (AudioMood.NEUTRAL to 0.0f)
        } catch (e: Exception) {
            Log.e(TAG, "Mood detection inference failed: ${e.message}")
            AudioMood.NEUTRAL to 0.0f
        }
    }
    
    /**
     * Extract audio features using TensorFlow Lite model
     */
    fun extractAudioFeatures(rawAudioData: FloatArray): FloatArray {
        return try {
            audioFeaturesInterpreter?.let { interpreter ->
                val outputArray = Array(1) { FloatArray(AUDIO_FEATURE_INPUT_SIZE) }
                val inputArray = Array(1) { rawAudioData }
                
                interpreter.run(inputArray, outputArray)
                outputArray[0]
            } ?: FloatArray(AUDIO_FEATURE_INPUT_SIZE) { 0.0f }
        } catch (e: Exception) {
            Log.e(TAG, "Audio feature extraction failed: ${e.message}")
            FloatArray(AUDIO_FEATURE_INPUT_SIZE) { 0.0f }
        }
    }
    
    /**
     * Generate EQ suggestions using neural network
     */
    fun generateEQSuggestions(audioFeatures: FloatArray): FloatArray {
        return try {
            eqSuggestionInterpreter?.let { interpreter ->
                val outputArray = Array(1) { FloatArray(EQ_OUTPUT_SIZE) }
                val inputArray = Array(1) { audioFeatures }
                
                interpreter.run(inputArray, outputArray)
                outputArray[0]
            } ?: FloatArray(EQ_OUTPUT_SIZE) { 0.0f }
        } catch (e: Exception) {
            Log.e(TAG, "EQ suggestion generation failed: ${e.message}")
            FloatArray(EQ_OUTPUT_SIZE) { 0.0f }
        }
    }
    
    /**
     * Check if models are loaded and ready for inference
     */
    fun areModelsLoaded(): Boolean {
        return isInitialized && (
            genreInterpreter != null || 
            moodInterpreter != null || 
            audioFeaturesInterpreter != null || 
            eqSuggestionInterpreter != null
        )
    }
    
    /**
     * Get detailed model loading status
     */
    fun getModelStatus(): ModelStatus {
        return ModelStatus(
            isInitialized = isInitialized,
            genreModelLoaded = genreInterpreter != null,
            moodModelLoaded = moodInterpreter != null,
            audioFeaturesModelLoaded = audioFeaturesInterpreter != null,
            eqSuggestionModelLoaded = eqSuggestionInterpreter != null,
            gpuAccelerationEnabled = gpuDelegate != null
        )
    }
    
    /**
     * Clean up resources and close interpreters
     */
    fun cleanupResources() {
        try {
            genreInterpreter?.close()
            moodInterpreter?.close()
            audioFeaturesInterpreter?.close()
            eqSuggestionInterpreter?.close()
            gpuDelegate?.close()
            
            genreInterpreter = null
            moodInterpreter = null
            audioFeaturesInterpreter = null
            eqSuggestionInterpreter = null
            gpuDelegate = null
            
            isInitialized = false
            Log.i(TAG, "TensorFlow Lite resources cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error during resource cleanup: ${e.message}")
        }
    }
    
    // Private helper methods
    
    private fun loadModel(modelFile: String, options: Interpreter.Options): Interpreter {
        return try {
            val fileDescriptor = context.assets.openFd(modelFile)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            
            Interpreter(modelBuffer, options)
        } catch (e: Exception) {
            throw RuntimeException("Failed to load model $modelFile: ${e.message}", e)
        }
    }
    
    private fun getGenreFromIndex(index: Int): String {
        val genres = arrayOf(
            "Electronic", "Rock", "Pop", "Hip-Hop", "Jazz", 
            "Classical", "Ambient", "Folk", "Metal", "Unknown"
        )
        return genres.getOrElse(index) { "Unknown" }
    }
}

/**
 * Data class representing the status of loaded TensorFlow Lite models
 */
data class ModelStatus(
    val isInitialized: Boolean,
    val genreModelLoaded: Boolean,
    val moodModelLoaded: Boolean,
    val audioFeaturesModelLoaded: Boolean,
    val eqSuggestionModelLoaded: Boolean,
    val gpuAccelerationEnabled: Boolean
)