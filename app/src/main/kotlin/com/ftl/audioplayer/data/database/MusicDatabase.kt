package com.ftl.audioplayer.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.ftl.audioplayer.data.dao.PlaylistDao
import com.ftl.audioplayer.data.dao.TrackDao
import com.ftl.audioplayer.data.entities.Playlist
import com.ftl.audioplayer.data.entities.PlaylistTrack
import com.ftl.audioplayer.data.entities.Track

@Database(
    entities = [Track::class, Playlist::class, PlaylistTrack::class],
    version = 2,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    
    companion object {
        @Volatile
        private var INSTANCE: MusicDatabase? = null
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the unique constraint
                database.execSQL("""
                    CREATE TABLE tracks_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        artist TEXT NOT NULL,
                        album TEXT NOT NULL,
                        duration INTEGER NOT NULL,
                        filePath TEXT NOT NULL,
                        fileSize INTEGER NOT NULL,
                        mimeType TEXT NOT NULL,
                        sampleRate INTEGER NOT NULL,
                        bitRate INTEGER NOT NULL,
                        channels INTEGER NOT NULL,
                        bitDepth INTEGER NOT NULL,
                        codec TEXT NOT NULL,
                        albumArt TEXT,
                        genre TEXT,
                        year INTEGER,
                        trackNumber INTEGER,
                        discNumber INTEGER,
                        dateAdded INTEGER NOT NULL,
                        lastPlayed INTEGER,
                        playCount INTEGER NOT NULL,
                        isHiRes INTEGER NOT NULL,
                        isFavorite INTEGER NOT NULL
                    )
                """)
                
                // Create unique index on filePath
                database.execSQL("CREATE UNIQUE INDEX index_tracks_new_filePath ON tracks_new(filePath)")
                
                // Copy data from old table, removing duplicates by using GROUP BY
                database.execSQL("""
                    INSERT INTO tracks_new
                    SELECT id, title, artist, album, duration, filePath, fileSize, mimeType,
                           sampleRate, bitRate, channels, bitDepth, codec, albumArt, genre,
                           year, trackNumber, discNumber, dateAdded, lastPlayed, playCount,
                           isHiRes, isFavorite
                    FROM tracks
                    WHERE id IN (
                        SELECT MIN(id) FROM tracks GROUP BY filePath
                    )
                """)
                
                // Drop old table
                database.execSQL("DROP TABLE tracks")
                
                // Rename new table
                database.execSQL("ALTER TABLE tracks_new RENAME TO tracks")
            }
        }
        
        fun getDatabase(context: Context): MusicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    "ftl_music_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}