package com.ftl.audioplayer.playback

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.ftl.audioplayer.data.entities.Track
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "MusicPlayer"
    }
    
    private var mediaPlayer: MediaPlayer? = null
    private var positionUpdateJob: Job? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()
    
    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()
    
    private var playlist: List<Track> = emptyList()
    private var currentIndex: Int = -1
    
    fun setPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        playlist = tracks
        currentIndex = startIndex
    }
    
    suspend fun playTrack(track: Track) {
        Log.d(TAG, "🎵 Playing track: ${track.title} by ${track.artist}")
        
        try {
            // Stop current playback and position updates
            stop()
            
            _isBuffering.value = true
            _currentTrack.value = track
            
            // Create new MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(track.filePath))
                setOnPreparedListener {
                    Log.d(TAG, "✅ Track prepared, starting playback")
                    _duration.value = duration.toLong()
                    _isBuffering.value = false
                    start()
                    _isPlaying.value = true
                    startPositionUpdates()
                }
                setOnCompletionListener {
                    Log.d(TAG, "🏁 Track completed")
                    stopPositionUpdates()
                    _isPlaying.value = false
                    _playbackPosition.value = 0L
                    // Auto-play next track if available
                    CoroutineScope(Dispatchers.Main).launch {
                        playNext()
                    }
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "❌ MediaPlayer error: what=$what, extra=$extra")
                    stopPositionUpdates()
                    _isPlaying.value = false
                    _isBuffering.value = false
                    true
                }
                prepareAsync()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error playing track: ${track.title}", e)
            _isPlaying.value = false
            _isBuffering.value = false
            _currentTrack.value = null
        }
    }
    
    suspend fun playNext() {
        if (playlist.isNotEmpty() && currentIndex < playlist.size - 1) {
            currentIndex++
            playTrack(playlist[currentIndex])
        }
    }
    
    suspend fun playPrevious() {
        if (playlist.isNotEmpty() && currentIndex > 0) {
            currentIndex--
            playTrack(playlist[currentIndex])
        }
    }
    
    fun seekTo(position: Long) {
        mediaPlayer?.let { player ->
            val safePosition = position.coerceIn(0, _duration.value)
            player.seekTo(safePosition.toInt())
            _playbackPosition.value = safePosition
            Log.d(TAG, "⏩ Seeking to ${safePosition}ms")
        }
    }
    
    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        _playbackPosition.value = player.currentPosition.toLong()
                    }
                }
                delay(100) // Update every 100ms
            }
        }
    }
    
    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }
    
    fun pause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                _isPlaying.value = false
                Log.d(TAG, "⏸️ Playback paused")
            }
        }
    }
    
    fun resume() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                player.start()
                _isPlaying.value = true
                Log.d(TAG, "▶️ Playback resumed")
            }
        }
    }
    
    fun stop() {
        stopPositionUpdates()
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
            Log.d(TAG, "⏹️ Playback stopped")
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentTrack.value = null
        _playbackPosition.value = 0L
        _duration.value = 0L
        _isBuffering.value = false
    }
    
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }
}