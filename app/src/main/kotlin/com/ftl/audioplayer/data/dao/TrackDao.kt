package com.ftl.audioplayer.data.dao

import androidx.room.*
import com.ftl.audioplayer.data.entities.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    
    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun getAllTracks(): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: Long): Track?
    
    @Query("SELECT * FROM tracks WHERE filePath = :filePath")
    suspend fun getTrackByPath(filePath: String): Track?
    
    @Query("SELECT * FROM tracks WHERE artist LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%'")
    fun searchTracks(query: String): Flow<List<Track>>
    
    @Query("SELECT DISTINCT artist FROM tracks WHERE artist != '' ORDER BY artist ASC")
    fun getAllArtists(): Flow<List<String>>
    
    @Query("SELECT DISTINCT album FROM tracks WHERE album != '' ORDER BY album ASC")  
    fun getAllAlbums(): Flow<List<String>>
    
    @Query("SELECT * FROM tracks WHERE artist = :artist ORDER BY album ASC, trackNumber ASC")
    fun getTracksByArtist(artist: String): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE album = :album ORDER BY trackNumber ASC")
    fun getTracksByAlbum(album: String): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteTracks(): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE isHiRes = 1 ORDER BY sampleRate DESC, bitDepth DESC")
    fun getHiResTracks(): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks ORDER BY lastPlayed DESC LIMIT :limit")
    fun getRecentTracks(limit: Int = 50): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks ORDER BY playCount DESC LIMIT :limit")
    fun getMostPlayedTracks(limit: Int = 50): Flow<List<Track>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<Track>)
    
    @Update
    suspend fun updateTrack(track: Track)
    
    @Query("UPDATE tracks SET playCount = playCount + 1, lastPlayed = :timestamp WHERE id = :trackId")
    suspend fun incrementPlayCount(trackId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE tracks SET isFavorite = :isFavorite WHERE id = :trackId")
    suspend fun setFavorite(trackId: Long, isFavorite: Boolean)
    
    @Delete
    suspend fun deleteTrack(track: Track)
    
    @Query("DELETE FROM tracks WHERE filePath = :filePath")
    suspend fun deleteTrackByPath(filePath: String)
    
    @Query("DELETE FROM tracks")
    suspend fun deleteAllTracks()
    
    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTrackCount(): Int
    
    @Query("SELECT SUM(duration) FROM tracks")
    suspend fun getTotalDuration(): Long?
}