package com.ftl.audioplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ftl.audioplayer.ui.theme.CyberAqua

@Composable
fun PlaylistsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.List,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = CyberAqua.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Playlists",
                style = MaterialTheme.typography.headlineMedium,
                color = CyberAqua
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coming Soon",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}