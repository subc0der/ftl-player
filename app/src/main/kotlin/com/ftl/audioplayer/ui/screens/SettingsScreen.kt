package com.ftl.audioplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ftl.audioplayer.ui.theme.GreenCyan
import com.ftl.audioplayer.ui.theme.CyberPink
import com.ftl.audioplayer.ui.theme.DarkIndigoPurple
import com.ftl.audioplayer.ui.theme.MediumIndigoPurple
import com.ftl.audioplayer.ui.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var showClearDialog by remember { mutableStateOf(false) }
    var showRescanDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = GreenCyan,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Library Management Section
        SettingsSection(title = "Music Library") {
            // Track count info
            val trackCount by viewModel.trackCount.collectAsState()
            SettingsItem(
                icon = Icons.Default.List,
                title = "Library Size",
                subtitle = "$trackCount tracks",
                onClick = null
            )
            
            Divider(color = Color.Gray.copy(alpha = 0.2f))
            
            // Rescan library
            SettingsItem(
                icon = Icons.Default.Refresh,
                title = "Rescan Library",
                subtitle = "Scan device for new music files",
                onClick = { showRescanDialog = true }
            )
            
            Divider(color = Color.Gray.copy(alpha = 0.2f))
            
            // Clear library
            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Clear Library",
                subtitle = "Remove all tracks from library",
                onClick = { showClearDialog = true },
                isDestructive = true
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Playback Settings Section
        SettingsSection(title = "Playback") {
            SettingsItem(
                icon = Icons.Default.Star,
                title = "Hi-Res Audio",
                subtitle = "Prioritize high-resolution playback",
                onClick = null,
                trailing = {
                    Switch(
                        checked = true,
                        onCheckedChange = { },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GreenCyan,
                            checkedTrackColor = GreenCyan.copy(alpha = 0.5f)
                        )
                    )
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // About Section
        SettingsSection(title = "About") {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "FTL Hi-Res Audio Player",
                subtitle = "Version 1.0.0",
                onClick = null
            )
        }
    }
    
    // Clear Library Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { 
                Text(
                    "Clear Music Library?",
                    color = GreenCyan
                )
            },
            text = { 
                Text(
                    "This will remove all tracks from your library. Your music files will not be deleted. You can rescan to add them back.",
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.clearLibrary()
                            showClearDialog = false
                        }
                    }
                ) {
                    Text("Clear", color = CyberPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = GreenCyan)
                }
            },
            containerColor = DarkIndigoPurple,
            iconContentColor = CyberPink
        )
    }
    
    // Rescan Library Dialog
    if (showRescanDialog) {
        AlertDialog(
            onDismissRequest = { showRescanDialog = false },
            title = { 
                Text(
                    "Rescan Music Library?",
                    color = GreenCyan
                )
            },
            text = { 
                Text(
                    "This will scan your device for music files and update the library. Existing tracks will be updated if metadata has changed.",
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.rescanLibrary(context)
                            showRescanDialog = false
                        }
                    }
                ) {
                    Text("Scan", color = GreenCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescanDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = DarkIndigoPurple,
            iconContentColor = GreenCyan
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = GreenCyan.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MediumIndigoPurple
            ),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)?,
    isDestructive: Boolean = false,
    trailing: @Composable (() -> Unit)? = null
) {
    val contentColor = when {
        isDestructive -> CyberPink
        else -> Color.White
    }
    
    val iconColor = when {
        isDestructive -> CyberPink.copy(alpha = 0.7f)
        else -> GreenCyan.copy(alpha = 0.7f)
    }
    
    if (onClick != null) {
        Surface(
            onClick = onClick,
            color = Color.Transparent
        ) {
            SettingsItemContent(
                icon = icon,
                title = title,
                subtitle = subtitle,
                contentColor = contentColor,
                iconColor = iconColor,
                trailing = trailing
            )
        }
    } else {
        SettingsItemContent(
            icon = icon,
            title = title,
            subtitle = subtitle,
            contentColor = contentColor,
            iconColor = iconColor,
            trailing = trailing
        )
    }
}

@Composable
private fun SettingsItemContent(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    contentColor: Color,
    iconColor: Color,
    trailing: @Composable (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        
        if (trailing != null) {
            trailing()
        }
    }
}