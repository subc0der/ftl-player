package com.ftl.audioplayer.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val trackCount: Int = 0,
    val totalDuration: Long = 0, // Total duration in milliseconds
    val coverArt: String? = null // Path to custom playlist cover
)