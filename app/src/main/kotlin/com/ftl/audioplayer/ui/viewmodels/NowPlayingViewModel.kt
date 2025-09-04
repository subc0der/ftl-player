package com.ftl.audioplayer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ftl.audioplayer.data.entities.Track
import com.ftl.audioplayer.data.repository.MusicRepository
import com.ftl.audioplayer.playback.MusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicPlayer: MusicPlayer,
    private val musicRepository: MusicRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "NowPlayingViewModel"
    }
    
    // Expose player states
    val currentTrack: StateFlow<Track?> = musicPlayer.currentTrack
    val isPlaying: StateFlow<Boolean> = musicPlayer.isPlaying
    val playbackPosition: StateFlow<Long> = musicPlayer.playbackPosition
    val duration: StateFlow<Long> = musicPlayer.duration
    val isBuffering: StateFlow<Boolean> = musicPlayer.isBuffering
    
    fun togglePlayPause() {
        musicPlayer.togglePlayPause()
    }
    
    fun seekTo(position: Long) {
        musicPlayer.seekTo(position)
    }
    
    fun playNext() {
        viewModelScope.launch {
            musicPlayer.playNext()
        }
    }
    
    fun playPrevious() {
        viewModelScope.launch {
            musicPlayer.playPrevious()
        }
    }
    
    fun setFavorite(isFavorite: Boolean) {
        currentTrack.value?.let { track ->
            viewModelScope.launch {
                musicRepository.setFavorite(track.id, isFavorite)
            }
        }
    }
    
    fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }
    
    fun getProgress(): Float {
        val duration = duration.value
        return if (duration > 0) {
            playbackPosition.value.toFloat() / duration
        } else {
            0f
        }
    }
}