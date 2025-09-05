package com.ftl.audioplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.ftl.audioplayer.data.dao.PlaylistDao
import com.ftl.audioplayer.data.dao.TrackDao
import com.ftl.audioplayer.data.entities.Playlist
import com.ftl.audioplayer.data.entities.PlaylistTrack
import com.ftl.audioplayer.data.entities.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao,
    private val context: Context
) {
    
    companion object {
        private const val TAG = "MusicRepository"
        private val SUPPORTED_FORMATS = setOf(
            "audio/mpeg", "audio/mp4", "audio/x-flac", "audio/ogg",
            "audio/wav", "audio/x-wav", "audio/aac", "audio/x-aac",
            "audio/3gpp", "audio/amr", "audio/x-ms-wma"
        )
    }
    
    // Track operations
    fun getAllTracks(): Flow<List<Track>> = trackDao.getAllTracks()
    
    fun getTracksByArtist(artist: String): Flow<List<Track>> = trackDao.getTracksByArtist(artist)
    
    fun getTracksByAlbum(album: String): Flow<List<Track>> = trackDao.getTracksByAlbum(album)
    
    fun getFavoriteTracks(): Flow<List<Track>> = trackDao.getFavoriteTracks()
    
    fun getHiResTracks(): Flow<List<Track>> = trackDao.getHiResTracks()
    
    fun searchTracks(query: String): Flow<List<Track>> = trackDao.searchTracks(query)
    
    suspend fun getTrackById(id: Long): Track? = trackDao.getTrackById(id)
    
    suspend fun incrementPlayCount(trackId: Long) = trackDao.incrementPlayCount(trackId)
    
    suspend fun setFavorite(trackId: Long, isFavorite: Boolean) = trackDao.setFavorite(trackId, isFavorite)
    
    // Playlist operations
    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()
    
    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>> = playlistDao.getPlaylistTracks(playlistId)
    
    suspend fun createPlaylist(name: String, description: String? = null): Long {
        val playlist = Playlist(name = name, description = description)
        return playlistDao.insertPlaylist(playlist)
    }
    
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val lastPosition = playlistDao.getLastPosition(playlistId) ?: -1
        val playlistTrack = PlaylistTrack(
            playlistId = playlistId,
            trackId = trackId,
            position = lastPosition + 1
        )
        playlistDao.addTrackToPlaylist(playlistTrack)
        playlistDao.updatePlaylistStats(playlistId)
    }
    
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long, position: Int) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)
        playlistDao.reorderAfterRemoval(playlistId, position)
        playlistDao.updatePlaylistStats(playlistId)
    }
    
    // Music library scanning
    suspend fun scanMusicLibrary(): Int = withContext(Dispatchers.IO) {
        try {
            android.util.Log.i(TAG, "🚀 Starting music library scan...")
            val tracks = discoverAudioFiles()
            android.util.Log.d(TAG, "📚 Processing ${tracks.size} discovered tracks...")
            
            // Process each track individually to handle duplicates
            var newTracks = 0
            var updatedTracks = 0
            
            tracks.forEach { track ->
                val existingTrack = trackDao.getTrackByPath(track.filePath)
                if (existingTrack == null) {
                    // New track, insert it
                    trackDao.insertTrack(track)
                    newTracks++
                } else {
                    // Track exists, update metadata if changed
                    val updatedTrack = existingTrack.copy(
                        title = track.title,
                        artist = track.artist,
                        album = track.album,
                        duration = track.duration,
                        fileSize = track.fileSize,
                        sampleRate = track.sampleRate,
                        bitRate = track.bitRate,
                        channels = track.channels,
                        bitDepth = track.bitDepth,
                        codec = track.codec,
                        isHiRes = track.isHiRes,
                        year = track.year,
                        trackNumber = track.trackNumber
                    )
                    trackDao.updateTrack(updatedTrack)
                    updatedTracks++
                }
            }
            
            android.util.Log.i(TAG, "✅ Scan complete: $newTracks new tracks added, $updatedTracks tracks updated")
            newTracks + updatedTracks
        } catch (e: Exception) {
            android.util.Log.e(TAG, "💥 Error scanning music library", e)
            throw e // Re-throw to let the ViewModel handle it
        }
    }
    
    suspend fun clearMusicLibrary() = withContext(Dispatchers.IO) {
        try {
            android.util.Log.i(TAG, "🗑️ Clearing music library...")
            trackDao.deleteAllTracks()
            android.util.Log.i(TAG, "✅ Music library cleared successfully")
        } catch (e: Exception) {
            android.util.Log.e(TAG, "💥 Error clearing music library", e)
            throw e
        }
    }
    
    private fun discoverAudioFiles(): List<Track> {
        val tracks = mutableListOf<Track>()
        
        Log.d(TAG, "Starting audio file discovery...")
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA, // File path
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        
        Log.d(TAG, "Querying MediaStore with selection: $selection")
        
        try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val count = cursor.count
                Log.d(TAG, "MediaStore query returned $count rows")
                
                var validTracks = 0
                var invalidTracks = 0
                
                while (cursor.moveToNext()) {
                    try {
                        val track = createTrackFromCursor(cursor)
                        if (track != null && isValidAudioFile(track.filePath, track.mimeType)) {
                            tracks.add(track)
                            validTracks++
                            Log.v(TAG, "Added track: ${track.title} (${track.filePath})")
                        } else {
                            invalidTracks++
                            Log.v(TAG, "Skipped invalid track: ${track?.filePath ?: "null"}")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error processing cursor row", e)
                        invalidTracks++
                    }
                }
                
                Log.i(TAG, "Discovery complete: $validTracks valid tracks, $invalidTracks invalid/skipped")
            } ?: run {
                Log.w(TAG, "MediaStore query returned null cursor")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception during MediaStore query", e)
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Exception during MediaStore query", e)
            throw e
        }
        
        return tracks
    }
    
    private fun createTrackFromCursor(cursor: Cursor): Track? {
        return try {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)) ?: "Unknown Title"
            val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)) ?: "Unknown Artist"
            val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)) ?: "Unknown Album"
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            val filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)) ?: return null
            val fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
            val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)) ?: "audio/unknown"
            val year = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
            val trackNumber = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
            val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)) * 1000 // Convert to milliseconds
            
            // Get additional metadata using MediaMetadataRetriever
            val (sampleRate, bitRate, channels, bitDepth, codec) = getAudioMetadata(filePath)
            
            Track(
                title = title,
                artist = artist,
                album = album,
                duration = duration,
                filePath = filePath,
                fileSize = fileSize,
                mimeType = mimeType,
                sampleRate = sampleRate,
                bitRate = bitRate,
                channels = channels,
                bitDepth = bitDepth,
                codec = codec,
                year = if (year > 0) year else null,
                trackNumber = if (trackNumber > 0) trackNumber else null,
                dateAdded = dateAdded,
                isHiRes = sampleRate > 48000 || bitDepth > 16
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error creating track from cursor", e)
            null
        }
    }
    
    private fun getAudioMetadata(filePath: String): AudioMetadata {
        var sampleRate = 44100
        var bitRate = 320000
        var channels = 2
        var bitDepth = 16
        var codec = "unknown"
        
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)?.let {
                sampleRate = it.toIntOrNull() ?: sampleRate
            }
            
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.let {
                bitRate = it.toIntOrNull() ?: bitRate
            }
            
            // MediaMetadataRetriever does not provide a reliable way to get the number of audio channels.
            // We'll use the default value (2 for stereo), or set based on codec if needed.
            
            // Codec information
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)?.let { mimeType ->
                codec = when {
                    mimeType.contains("flac") -> "FLAC"
                    mimeType.contains("mp3") || mimeType.contains("mpeg") -> "MP3"
                    mimeType.contains("aac") -> "AAC"
                    mimeType.contains("ogg") -> "OGG"
                    mimeType.contains("wav") -> "WAV"
                    else -> codec
                }
            }
            
            // Estimate bit depth for lossless formats
            if (codec == "FLAC" || codec == "WAV") {
                bitDepth = when {
                    bitRate > 2000000 -> 24 // Likely 24-bit
                    bitRate > 1000000 -> 16 // Likely 16-bit
                    else -> 16
                }
            }
            
            retriever.release()
        } catch (e: Exception) {
            Log.w(TAG, "Error extracting metadata for $filePath", e)
        }
        
        return AudioMetadata(sampleRate, bitRate, channels, bitDepth, codec)
    }
    
    private fun isValidAudioFile(filePath: String, mimeType: String): Boolean {
        if (!SUPPORTED_FORMATS.contains(mimeType)) return false
        
        val file = File(filePath)
        if (!file.exists() || !file.canRead()) return false
        
        // Check minimum file size (1KB)
        if (file.length() < 1024) return false
        
        return true
    }
    
    private data class AudioMetadata(
        val sampleRate: Int,
        val bitRate: Int,
        val channels: Int,
        val bitDepth: Int,
        val codec: String
    )
}