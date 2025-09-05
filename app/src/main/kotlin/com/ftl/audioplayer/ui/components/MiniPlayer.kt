package com.ftl.audioplayer.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ftl.audioplayer.ui.theme.DeepPurple
import com.ftl.audioplayer.ui.theme.GreenCyan
import com.ftl.audioplayer.ui.theme.DarkIndigoPurple
import com.ftl.audioplayer.ui.theme.MediumIndigoPurple
import com.ftl.audioplayer.ui.viewmodels.NowPlayingViewModel

@Composable
fun MiniPlayer(
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackPosition by viewModel.playbackPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val progress by remember(playbackPosition, duration) {
        derivedStateOf {
            if (duration > 0) playbackPosition.toFloat() / duration else 0f
        }
    }
    
    AnimatedVisibility(
        visible = currentTrack != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        currentTrack?.let { track ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clickable { onExpandClick() },
                colors = CardDefaults.cardColors(
                    containerColor = MediumIndigoPurple.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ) {
                Column {
                    // Progress bar
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth(),
                        color = GreenCyan,
                        trackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Album art placeholder
                        Card(
                            modifier = Modifier.size(48.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = DeepPurple.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = GreenCyan.copy(alpha = 0.7f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // Track info
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = track.artist,
                                style = MaterialTheme.typography.bodySmall,
                                color = GreenCyan.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        // Hi-Res indicator
                        if (track.isHiRes) {
                            Badge(
                                containerColor = GreenCyan.copy(alpha = 0.2f),
                                contentColor = GreenCyan,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = "HI-RES",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        
                        // Play/Pause button
                        IconButton(
                            onClick = { viewModel.togglePlayPause() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = GreenCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}