package com.ftl.audioplayer.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ftl.audioplayer.utils.PermissionUtils
import com.ftl.audioplayer.data.entities.Track
import com.ftl.audioplayer.ui.components.TrackItem
import com.ftl.audioplayer.ui.theme.GreenCyan
import com.ftl.audioplayer.ui.theme.DarkIndigoPurple
import com.ftl.audioplayer.ui.theme.MediumIndigoPurple
import com.ftl.audioplayer.ui.viewmodels.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigateToNowPlaying: () -> Unit = {},
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val selectedView by viewModel.selectedView.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val hasPermissions by viewModel.hasPermissions.collectAsStateWithLifecycle()
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        viewModel.checkPermissions() // Always recheck permissions
        if (allGranted) {
            // Automatically start scanning after permissions granted
            viewModel.scanLibrary()
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.initializeLibrary()
    }
    
    // Error snackbar
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // You could show a snackbar here if you had a SnackbarHost
            // For now we'll show it in the UI below
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Header with scan button - optimized for Pixel 9 Pro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Music Library",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GreenCyan
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hi-Res Audiophile Player",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Button(
                onClick = {
                    if (hasPermissions) {
                        viewModel.scanLibrary()
                    } else {
                        permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
                    }
                },
                enabled = !isScanning,
                modifier = Modifier
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenCyan,
                    contentColor = Color.Black
                )
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scanning...", style = MaterialTheme.typography.labelLarge)
                } else {
                    Icon(
                        imageVector = if (hasPermissions) Icons.Default.Refresh else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasPermissions) "Scan" else "Grant Access",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // View selection tabs - optimized for Pixel 9 Pro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LibraryTab(
                title = "Songs",
                isSelected = selectedView == LibraryView.SONGS,
                onClick = { viewModel.setView(LibraryView.SONGS) },
                modifier = Modifier.weight(1f)
            )
            LibraryTab(
                title = "Albums",
                isSelected = selectedView == LibraryView.ALBUMS,
                onClick = { viewModel.setView(LibraryView.ALBUMS) },
                modifier = Modifier.weight(1f)
            )
            LibraryTab(
                title = "Artists",
                isSelected = selectedView == LibraryView.ARTISTS,
                onClick = { viewModel.setView(LibraryView.ARTISTS) },
                modifier = Modifier.weight(1f)
            )
            LibraryTab(
                title = "Hi-Res",
                isSelected = selectedView == LibraryView.HI_RES,
                onClick = { viewModel.setView(LibraryView.HI_RES) },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Error message
        errorMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = message,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.clearError() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        
        // Content
        when {
            !hasPermissions -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Media Access Required",
                            style = MaterialTheme.typography.headlineSmall,
                            color = GreenCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Grant media permissions to scan\nfor music files on your device",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FilledTonalButton(
                            onClick = {
                                permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = GreenCyan.copy(alpha = 0.2f),
                                contentColor = GreenCyan
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Grant Permissions")
                        }
                    }
                }
            }
            
            isScanning && tracks.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = GreenCyan,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Scanning music library...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            tracks.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No music found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap 'Scan' to search for music files",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(tracks) { track ->
                        TrackItem(
                            track = track,
                            onTrackClick = { 
                                viewModel.playTrack(track)
                                onNavigateToNowPlaying()
                            },
                            onFavoriteClick = { viewModel.toggleFavorite(track) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onClick,
        label = { 
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            ) 
        },
        selected = isSelected,
        modifier = modifier
            .height(32.dp)
            .wrapContentWidth()
            .defaultMinSize(minWidth = 48.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GreenCyan.copy(alpha = 0.3f),
            selectedLabelColor = GreenCyan,
            containerColor = MediumIndigoPurple.copy(alpha = 0.6f),
            labelColor = Color.Gray
        ),
        border = FilterChipDefaults.filterChipBorder(
            selectedBorderColor = GreenCyan,
            borderColor = Color.Gray.copy(alpha = 0.5f),
            borderWidth = 1.dp
        )
    )
}

enum class LibraryView {
    SONGS, ALBUMS, ARTISTS, HI_RES
}