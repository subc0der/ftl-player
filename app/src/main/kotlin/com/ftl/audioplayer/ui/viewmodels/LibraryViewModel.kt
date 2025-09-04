package com.ftl.audioplayer.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ftl.audioplayer.data.entities.Track
import com.ftl.audioplayer.data.repository.MusicRepository
import com.ftl.audioplayer.playback.MusicPlayer
import com.ftl.audioplayer.ui.screens.LibraryView
import com.ftl.audioplayer.utils.PermissionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val musicPlayer: MusicPlayer,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    companion object {
        private const val TAG = "LibraryViewModel"
    }
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()
    
    private val _selectedView = MutableStateFlow(LibraryView.SONGS)
    val selectedView = _selectedView.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    
    private val _hasPermissions = MutableStateFlow(false)
    val hasPermissions = _hasPermissions.asStateFlow()
    
    private val allTracks = musicRepository.getAllTracks()
    private val hiResTracks = musicRepository.getHiResTracks()
    
    val tracks: StateFlow<List<Track>> = combine(
        allTracks,
        hiResTracks,
        selectedView
    ) { all, hiRes, view ->
        when (view) {
            LibraryView.SONGS -> all
            LibraryView.HI_RES -> hiRes
            LibraryView.ALBUMS -> all.sortedBy { "${it.album} - ${it.trackNumber}" }
            LibraryView.ARTISTS -> all.sortedBy { "${it.artist} - ${it.album} - ${it.trackNumber}" }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun initializeLibrary() {
        checkPermissions()
        viewModelScope.launch {
            // Only auto-scan if we have permissions and no tracks exist
            if (hasPermissions.value && tracks.value.isEmpty()) {
                scanLibrary()
            }
        }
    }
    
    fun checkPermissions() {
        _hasPermissions.value = PermissionUtils.hasMediaPermissions(context)
        Log.d(TAG, "Permissions check: ${_hasPermissions.value}")
    }
    
    fun scanLibrary() {
        viewModelScope.launch {
            android.util.Log.d(TAG, "🎵 scanLibrary() called - starting music discovery")
            _errorMessage.value = null
            
            // Show immediate feedback that scan is starting
            _errorMessage.value = "Starting scan..."
            
            if (!PermissionUtils.hasMediaPermissions(context)) {
                val missing = PermissionUtils.getMissingPermissions(context)
                _errorMessage.value = "Missing permissions: ${missing.joinToString(", ")}"
                android.util.Log.w(TAG, "🚫 Cannot scan - missing permissions: $missing")
                return@launch
            }
            
            android.util.Log.d(TAG, "✅ Permissions OK, starting scan...")
            _isScanning.value = true
            _errorMessage.value = "Scanning device for music files..."
            
            try {
                android.util.Log.d(TAG, "📁 Calling musicRepository.scanMusicLibrary()...")
                val scannedCount = musicRepository.scanMusicLibrary()
                android.util.Log.i(TAG, "✅ Library scan completed: $scannedCount tracks found")
                
                if (scannedCount == 0) {
                    _errorMessage.value = "No music files found on device. Try adding some MP3/FLAC files to your Music folder."
                } else {
                    _errorMessage.value = "Scan complete! Found $scannedCount music files."
                    android.util.Log.d(TAG, "🎉 Scan successful - found $scannedCount tracks")
                }
            } catch (e: SecurityException) {
                android.util.Log.e(TAG, "🚫 Security exception during scan", e)
                _errorMessage.value = "Permission denied: ${e.message}"
            } catch (e: Exception) {
                android.util.Log.e(TAG, "❌ Error scanning music library", e)
                _errorMessage.value = "Scan failed: ${e.message}"
            } finally {
                android.util.Log.d(TAG, "🏁 Scan finished, setting isScanning = false")
                _isScanning.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun setView(view: LibraryView) {
        _selectedView.value = view
    }
    
    fun playTrack(track: Track) {
        viewModelScope.launch {
            android.util.Log.d(TAG, "🎵 User tapped track: ${track.title} by ${track.artist}")
            
            // Increment play count in database
            musicRepository.incrementPlayCount(track.id)
            
            // Set the current playlist (all tracks) and find the index
            val allTracks = tracks.value
            val trackIndex = allTracks.indexOf(track)
            if (trackIndex >= 0) {
                musicPlayer.setPlaylist(allTracks, trackIndex)
            }
            
            // Play the track
            musicPlayer.playTrack(track)
        }
    }
    
    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            musicRepository.setFavorite(track.id, !track.isFavorite)
        }
    }
}