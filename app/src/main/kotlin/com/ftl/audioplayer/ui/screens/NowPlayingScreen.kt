package com.ftl.audioplayer.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ftl.audioplayer.ui.theme.DeepPurple
import com.ftl.audioplayer.ui.theme.GreenCyan
import com.ftl.audioplayer.ui.theme.CyberPink
import com.ftl.audioplayer.ui.theme.DarkIndigoPurple
import com.ftl.audioplayer.ui.theme.MediumIndigoPurple
import com.ftl.audioplayer.ui.viewmodels.NowPlayingViewModel
import kotlinx.coroutines.launch

@Composable
fun NowPlayingScreen(
    onBackClick: () -> Unit,
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackPosition by viewModel.playbackPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val isBuffering by viewModel.isBuffering.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkIndigoPurple)
    ) {
        // Animated background gradient
        AnimatedBackground(isPlaying = isPlaying)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding()
        ) {
            // Top Bar
            TopBar(onBackClick = onBackClick)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Album Art with Visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Animated ring visualizer
                if (isPlaying) {
                    AudioVisualizer()
                }
                
                // Album art placeholder
                Card(
                    modifier = Modifier
                        .fillMaxSize(0.75f)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MediumIndigoPurple
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (isPlaying) GreenCyan else Color.Gray
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = Color.Gray.copy(alpha = 0.3f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Track Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentTrack?.let { track ->
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodyLarge,
                        color = GreenCyan,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = track.album,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Hi-Res indicator
                    if (track.isHiRes) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Badge(
                            containerColor = CyberPink.copy(alpha = 0.2f),
                            contentColor = CyberPink
                        ) {
                            Text(
                                text = "HI-RES • ${track.sampleRate/1000}kHz • ${track.bitDepth}bit",
                                fontSize = 10.sp
                            )
                        }
                    }
                } ?: run {
                    Text(
                        text = "No track playing",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Seek Bar
            SeekBar(
                position = playbackPosition,
                duration = duration,
                onSeek = { viewModel.seekTo(it) },
                formatTime = { viewModel.formatTime(it) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Playback Controls
            PlaybackControls(
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                onPlayPause = { viewModel.togglePlayPause() },
                onNext = { viewModel.playNext() },
                onPrevious = { viewModel.playPrevious() }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom Actions
            BottomActions(
                isFavorite = currentTrack?.isFavorite ?: false,
                onFavoriteClick = { viewModel.setFavorite(!it) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        
        Text(
            text = "NOW PLAYING",
            style = MaterialTheme.typography.labelLarge,
            color = GreenCyan,
            letterSpacing = 2.sp
        )
        
        IconButton(onClick = { /* TODO: Show queue */ }) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Queue",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun AnimatedBackground(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (isPlaying) {
            val gradient = Brush.radialGradient(
                colors = listOf(
                    GreenCyan.copy(alpha = 0.1f),
                    CyberPink.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(size.width / 2, size.height / 3),
                radius = size.minDimension
            )
            drawRect(gradient)
        }
    }
}

@Composable
private fun AudioVisualizer() {
    val infiniteTransition = rememberInfiniteTransition()
    
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .rotate(rotation)
    ) {
        // Outer ring
        drawCircle(
            color = GreenCyan.copy(alpha = 0.3f),
            radius = size.minDimension / 2 * scale1,
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Inner ring
        drawCircle(
            color = CyberPink.copy(alpha = 0.2f),
            radius = size.minDimension / 2 * scale2 * 0.9f,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Composable
private fun SeekBar(
    position: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    formatTime: (Long) -> String
) {
    Column {
        Slider(
            value = if (duration > 0) position.toFloat() else 0f,
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
            colors = SliderDefaults.colors(
                thumbColor = GreenCyan,
                activeTrackColor = GreenCyan,
                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(position),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = formatTime(duration),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    isBuffering: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous
        IconButton(
            onClick = onPrevious,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        
        // Play/Pause
        FilledIconButton(
            onClick = onPlayPause,
            modifier = Modifier.size(72.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = GreenCyan
            ),
            enabled = !isBuffering
        ) {
            if (isBuffering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        
        // Next
        IconButton(
            onClick = onNext,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun BottomActions(
    isFavorite: Boolean,
    onFavoriteClick: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { /* TODO: Add to playlist */ }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to Playlist",
                tint = Color.White
            )
        }
        
        IconButton(onClick = { onFavoriteClick(isFavorite) }) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                tint = if (isFavorite) CyberPink else Color.White
            )
        }
        
        IconButton(onClick = { /* TODO: Share */ }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.White
            )
        }
        
        IconButton(onClick = { /* TODO: Show more options */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Options",
                tint = Color.White
            )
        }
    }
}