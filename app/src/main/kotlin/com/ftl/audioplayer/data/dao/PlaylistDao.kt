package com.ftl.audioplayer.data.dao

import androidx.room.*
import com.ftl.audioplayer.data.entities.Playlist
import com.ftl.audioplayer.data.entities.PlaylistTrack
import com.ftl.audioplayer.data.entities.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>
    
    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): Playlist?
    
    @Query("""
        SELECT t.* FROM tracks t 
        INNER JOIN playlist_tracks pt ON t.id = pt.trackId 
        WHERE pt.playlistId = :playlistId 
        ORDER BY pt.position ASC
    """)
    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>>
    
    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY position ASC")
    suspend fun getPlaylistTrackEntries(playlistId: Long): List<PlaylistTrack>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long
    
    @Update
    suspend fun updatePlaylist(playlist: Playlist)
    
    @Delete
    suspend fun deletePlaylist(playlist: Playlist)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylist(playlistTrack: PlaylistTrack)
    
    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    
    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)
    
    @Query("""
        UPDATE playlist_tracks 
        SET position = position - 1 
        WHERE playlistId = :playlistId AND position > :removedPosition
    """)
    suspend fun reorderAfterRemoval(playlistId: Long, removedPosition: Int)
    
    @Query("SELECT MAX(position) FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getLastPosition(playlistId: Long): Int?
    
    @Query("""
        UPDATE playlists 
        SET trackCount = (SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId),
            totalDuration = (
                SELECT COALESCE(SUM(t.duration), 0) 
                FROM tracks t 
                INNER JOIN playlist_tracks pt ON t.id = pt.trackId 
                WHERE pt.playlistId = :playlistId
            ),
            modifiedAt = :timestamp
        WHERE id = :playlistId
    """)
    suspend fun updatePlaylistStats(playlistId: Long, timestamp: Long = System.currentTimeMillis())
}