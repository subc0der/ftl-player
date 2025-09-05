package com.ftl.audioplayer.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ftl.audioplayer.data.repository.MusicRepository
import com.ftl.audioplayer.utils.PermissionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "SettingsViewModel"
    }
    
    private val _trackCount = MutableStateFlow(0)
    val trackCount: StateFlow<Int> = _trackCount.asStateFlow()
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()
    
    init {
        loadTrackCount()
    }
    
    private fun loadTrackCount() {
        viewModelScope.launch {
            musicRepository.getAllTracks().collect { tracks ->
                _trackCount.value = tracks.size
            }
        }
    }
    
    suspend fun clearLibrary() {
        try {
            Log.i(TAG, "🗑️ Clearing music library...")
            musicRepository.clearMusicLibrary()
            _message.value = "Library cleared successfully"
            Log.i(TAG, "✅ Library cleared")
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error clearing library", e)
            _message.value = "Failed to clear library: ${e.message}"
        }
    }
    
    suspend fun rescanLibrary(context: Context) {
        if (_isScanning.value) {
            Log.w(TAG, "⚠️ Scan already in progress")
            return
        }
        
        if (!PermissionUtils.hasMediaPermissions(context)) {
            Log.w(TAG, "⚠️ No media permission for library scan")
            _message.value = "Media permission required to scan library"
            return
        }
        
        _isScanning.value = true
        try {
            Log.i(TAG, "📱 Starting library rescan...")
            val count = musicRepository.scanMusicLibrary()
            _message.value = "Library scan complete: $count tracks processed"
            Log.i(TAG, "✅ Rescan complete: $count tracks")
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error during rescan", e)
            _message.value = "Scan failed: ${e.message}"
        } finally {
            _isScanning.value = false
        }
    }
    
    fun clearMessage() {
        _message.value = null
    }
}