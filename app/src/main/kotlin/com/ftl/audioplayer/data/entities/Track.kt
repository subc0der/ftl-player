package com.ftl.audioplayer.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [Index(value = ["filePath"], unique = true)]
)
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long, // Duration in milliseconds
    val filePath: String,
    val fileSize: Long,
    val mimeType: String,
    val sampleRate: Int = 44100,
    val bitRate: Int = 320000,
    val channels: Int = 2,
    val bitDepth: Int = 16,
    val codec: String = "unknown",
    val albumArt: String? = null, // Path to album art file
    val genre: String? = null,
    val year: Int? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val lastPlayed: Long? = null,
    val playCount: Int = 0,
    val isHiRes: Boolean = false, // Sample rate > 48kHz or bit depth > 16
    val isFavorite: Boolean = false
)