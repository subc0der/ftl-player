package com.ftl.audioplayer.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.ftl.audioplayer.ui.theme.Cyan
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
                .systemBarsPadding()
        ) {
            // Top Bar
            TopBar(
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
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
                
                // Album art with AsyncImage
                Card(
                    modifier = Modifier
                        .fillMaxSize(0.75f)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MediumIndigoPurple
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (isPlaying) Cyan else Color.Gray
                    )
                ) {
                    currentTrack?.let { track ->
                        if (track.albumArt != null) {
                            AsyncImage(
                                model = track.albumArt,
                                contentDescription = "Album art for ${track.title}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AlbumArtPlaceholder()
                        }
                    } ?: run {
                        AlbumArtPlaceholder()
                    }
                }
            }
            
                Spacer(modifier = Modifier.height(24.dp))
                
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
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Cyan,
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
            }  // End of scrollable column
            
            // Fixed bottom controls (outside scroll area)
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                // Seek Bar
                SeekBar(
                    position = playbackPosition,
                    duration = duration,
                    onSeek = { viewModel.seekTo(it) },
                    formatTime = { viewModel.formatTime(it) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Playback Controls
                PlaybackControls(
                    isPlaying = isPlaying,
                    isBuffering = isBuffering,
                    onPlayPause = { viewModel.togglePlayPause() },
                    onNext = { viewModel.playNext() },
                    onPrevious = { viewModel.playPrevious() }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Bottom Actions
                BottomActions(
                    isFavorite = currentTrack?.isFavorite ?: false,
                    onFavoriteClick = { viewModel.setFavorite(!it) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
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
        
        IconButton(
            onClick = { /* TODO: Show queue */ },
            enabled = false
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Queue",
                tint = Color.Gray.copy(alpha = 0.5f)
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
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        
        // Play/Stop (using Close icon temporarily for Stop until pause is implemented)
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
                    contentDescription = if (isPlaying) "Stop" else "Play",
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
                imageVector = Icons.Default.KeyboardArrowRight,
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
        IconButton(
            onClick = { /* TODO: Add to playlist */ },
            enabled = false
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to Playlist",
                tint = Color.Gray.copy(alpha = 0.5f)
            )
        }
        
        IconButton(onClick = { onFavoriteClick(isFavorite) }) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                tint = if (isFavorite) CyberPink else Color.White
            )
        }
        
        IconButton(
            onClick = { /* TODO: Share */ },
            enabled = false
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.Gray.copy(alpha = 0.5f)
            )
        }
        
        IconButton(
            onClick = { /* TODO: Show more options */ },
            enabled = false
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Options",
                tint = Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun AlbumArtPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = Cyan.copy(alpha = 0.4f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "♪ ♫ ♪",
                fontSize = 24.sp,
                color = Cyan.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun ScrollingText(
    text: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.White,
    fontWeight: FontWeight? = null
) {
    var shouldScroll by remember { mutableStateOf(false) }
    val maxWidth = 280.dp
    
    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (shouldScroll) -300f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (shouldScroll) 8000 else 0,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier
            .width(maxWidth)
            .height(style.fontSize.value.dp * 1.5f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (shouldScroll) "$text     $text" else text,
            style = style,
            color = color,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            textAlign = if (shouldScroll) TextAlign.Start else TextAlign.Center,
            modifier = Modifier
                .offset(x = if (shouldScroll) animatedOffset.dp else 0.dp)
        )
    }
}