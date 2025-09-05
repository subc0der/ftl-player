package com.ftl.audioplayer.playback

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.ftl.audioplayer.data.entities.Track
import com.ftl.audioplayer.service.FTLAudioService
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
    private var isTransitioning: Boolean = false
    
    fun setPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        playlist = tracks
        currentIndex = startIndex
        Log.d(TAG, "📋 Playlist set: ${tracks.size} tracks, starting at index $startIndex")
        if (tracks.isNotEmpty() && startIndex < tracks.size) {
            Log.d(TAG, "🎯 Current track will be: ${tracks[startIndex].title}")
        }
    }
    
    suspend fun playTrack(track: Track) {
        Log.d(TAG, "🎵 Playing track: ${track.title} by ${track.artist}")
        
        try {
            // Stop current playback and position updates
            Log.d(TAG, "🛑 Stopping any existing MediaPlayer...")
            stop()
            
            // Add small delay to ensure MediaPlayer is fully released
            kotlinx.coroutines.delay(200)
            
            _isBuffering.value = true
            _currentTrack.value = track
            
            Log.d(TAG, "🎵 Starting audio service...")
            startAudioService()
            
            // Create new MediaPlayer
            Log.d(TAG, "📱 Creating new MediaPlayer instance...")
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
        try {
            // Prevent rapid-fire track changes
            if (isTransitioning) {
                Log.w(TAG, "⚠️ Already transitioning to another track, ignoring playNext()")
                return
            }
            
            Log.d(TAG, "⏭️ Attempting to play next track. Current index: $currentIndex, Playlist size: ${playlist.size}")
            if (playlist.isNotEmpty() && currentIndex < playlist.size - 1) {
                isTransitioning = true
                currentIndex++
                Log.d(TAG, "✅ Playing next track at index $currentIndex: ${playlist[currentIndex].title}")
                playTrack(playlist[currentIndex])
                // Reset transition flag after a delay
                kotlinx.coroutines.delay(500)
                isTransitioning = false
            } else {
                Log.w(TAG, "⚠️ Cannot play next: at end of playlist or playlist empty")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error in playNext()", e)
            isTransitioning = false
        }
    }
    
    suspend fun playPrevious() {
        try {
            // Prevent rapid-fire track changes
            if (isTransitioning) {
                Log.w(TAG, "⚠️ Already transitioning to another track, ignoring playPrevious()")
                return
            }
            
            Log.d(TAG, "⏮️ Attempting to play previous track. Current index: $currentIndex, Playlist size: ${playlist.size}")
            if (playlist.isNotEmpty() && currentIndex > 0) {
                isTransitioning = true
                currentIndex--
                Log.d(TAG, "✅ Playing previous track at index $currentIndex: ${playlist[currentIndex].title}")
                playTrack(playlist[currentIndex])
                // Reset transition flag after a delay
                kotlinx.coroutines.delay(500)
                isTransitioning = false
            } else {
                Log.w(TAG, "⚠️ Cannot play previous: at beginning of playlist or playlist empty")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error in playPrevious()", e)
            isTransitioning = false
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
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
                Log.d(TAG, "⏹️ Playback stopped and MediaPlayer released")
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Error stopping MediaPlayer (ignoring): ${e.message}")
            }
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentTrack.value = null
        _playbackPosition.value = 0L
        _duration.value = 0L
        _isBuffering.value = false
        stopAudioService()
    }
    
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }
    
    private fun startAudioService() {
        val serviceIntent = Intent(context, FTLAudioService::class.java)
        context.startForegroundService(serviceIntent)
        Log.d(TAG, "🎵 Started FTL Audio Service")
    }
    
    private fun stopAudioService() {
        val serviceIntent = Intent(context, FTLAudioService::class.java)
        context.stopService(serviceIntent)
        Log.d(TAG, "🛑 Stopped FTL Audio Service")
    }
}