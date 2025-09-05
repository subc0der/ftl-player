package com.ftl.audioplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.ftl.audioplayer.R
import com.ftl.audioplayer.data.entities.Track
import com.ftl.audioplayer.playback.MusicPlayer
import com.ftl.audioplayer.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FTLAudioService : LifecycleService() {
    
    companion object {
        private const val TAG = "FTLAudioService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "ftl_audio_playback"
        private const val MEDIA_SESSION_TAG = "FTLAudioPlayer"
        
        const val ACTION_PLAY = "com.ftl.audioplayer.PLAY"
        const val ACTION_PAUSE = "com.ftl.audioplayer.PAUSE"
        const val ACTION_NEXT = "com.ftl.audioplayer.NEXT"
        const val ACTION_PREVIOUS = "com.ftl.audioplayer.PREVIOUS"
        const val ACTION_STOP = "com.ftl.audioplayer.STOP"
    }
    
    @Inject
    lateinit var musicPlayer: MusicPlayer
    
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationManager: NotificationManager
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "🎵 FTL Audio Service created")
        
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
        setupMediaSession()
        observeMusicPlayer()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        
        try {
            when (intent?.action) {
                ACTION_PLAY -> {
                    Log.d(TAG, "🎵 Service received PLAY command")
                    musicPlayer.togglePlayPause()
                }
                ACTION_PAUSE -> {
                    Log.d(TAG, "⏸️ Service received PAUSE command")
                    musicPlayer.togglePlayPause()
                }
                ACTION_NEXT -> {
                    Log.d(TAG, "⏭️ Service received NEXT command")
                    lifecycleScope.launch { 
                        try {
                            musicPlayer.playNext() 
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error executing NEXT command in service", e)
                        }
                    }
                }
                ACTION_PREVIOUS -> {
                    Log.d(TAG, "⏮️ Service received PREVIOUS command")
                    lifecycleScope.launch { 
                        try {
                            musicPlayer.playPrevious() 
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error executing PREVIOUS command in service", e)
                        }
                    }
                }
                ACTION_STOP -> {
                    Log.d(TAG, "⏹️ Service received STOP command")
                    musicPlayer.stop()
                    stopSelf()
                    return START_NOT_STICKY
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error in onStartCommand", e)
        }
        
        return START_STICKY
    }
    
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "🛑 FTL Audio Service destroyed")
        mediaSession.release()
        musicPlayer.stop()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "FTL Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for FTL Hi-Res Audio Player"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, MEDIA_SESSION_TAG).apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    musicPlayer.togglePlayPause()
                }
                
                override fun onPause() {
                    musicPlayer.togglePlayPause()
                }
                
                override fun onSkipToNext() {
                    lifecycleScope.launch { musicPlayer.playNext() }
                }
                
                override fun onSkipToPrevious() {
                    lifecycleScope.launch { musicPlayer.playPrevious() }
                }
                
                override fun onStop() {
                    musicPlayer.stop()
                    stopSelf()
                }
                
                override fun onSeekTo(pos: Long) {
                    musicPlayer.seekTo(pos)
                }
            })
            
            isActive = true
        }
    }
    
    private fun observeMusicPlayer() {
        lifecycleScope.launch {
            musicPlayer.currentTrack.collectLatest { track ->
                updateMediaMetadata(track)
                updateNotification(track, musicPlayer.isPlaying.value)
            }
        }
        
        lifecycleScope.launch {
            musicPlayer.isPlaying.collectLatest { isPlaying ->
                updatePlaybackState(isPlaying, musicPlayer.playbackPosition.value)
                updateNotification(musicPlayer.currentTrack.value, isPlaying)
            }
        }
        
        lifecycleScope.launch {
            musicPlayer.playbackPosition.collectLatest { position ->
                updatePlaybackState(musicPlayer.isPlaying.value, position)
            }
        }
    }
    
    private fun updateMediaMetadata(track: Track?) {
        val metadata = MediaMetadataCompat.Builder().apply {
            track?.let {
                putString(MediaMetadataCompat.METADATA_KEY_TITLE, it.title)
                putString(MediaMetadataCompat.METADATA_KEY_ARTIST, it.artist)
                putString(MediaMetadataCompat.METADATA_KEY_ALBUM, it.album)
                putLong(MediaMetadataCompat.METADATA_KEY_DURATION, it.duration)
                // TODO: Add album art bitmap when available
            }
        }.build()
        
        mediaSession.setMetadata(metadata)
    }
    
    private fun updatePlaybackState(isPlaying: Boolean, position: Long) {
        val state = if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        
        val playbackState = PlaybackStateCompat.Builder()
            .setState(state, position, 1.0f)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_SEEK_TO
            )
            .build()
            
        mediaSession.setPlaybackState(playbackState)
    }
    
    private fun updateNotification(track: Track?, isPlaying: Boolean) {
        if (track == null) {
            stopForeground(true)
            return
        }
        
        val notification = createNotification(track, isPlaying)
        startForeground(NOTIFICATION_ID, notification)
    }
    
    private fun createNotification(track: Track, isPlaying: Boolean): Notification {
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "Pause",
                createPendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Play", 
                createPendingIntent(ACTION_PLAY)
            )
        }
        
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(track.title)
            .setContentText("${track.artist} • ${track.album}")
            .setSubText("FTL Hi-Res Audio Player")
            .setContentIntent(contentIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(isPlaying)
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_previous,
                    "Previous",
                    createPendingIntent(ACTION_PREVIOUS)
                )
            )
            .addAction(playPauseAction)
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_next,
                    "Next",
                    createPendingIntent(ACTION_NEXT)
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .build()
    }
    
    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, FTLAudioService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}