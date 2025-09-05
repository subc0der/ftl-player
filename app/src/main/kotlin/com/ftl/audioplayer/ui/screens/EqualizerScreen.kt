package com.ftl.audioplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ftl.audioplayer.ui.theme.GreenCyan
import com.ftl.audioplayer.ui.theme.DarkIndigoPurple
import com.ftl.audioplayer.ui.theme.MediumIndigoPurple

@Composable
fun EqualizerScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        DarkIndigoPurple.copy(alpha = 0.3f),
                        Color.Black
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Equalizer",
                tint = GreenCyan,
                modifier = Modifier
                    .size(120.dp)
                    .alpha(0.7f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            Text(
                text = "10-Band Equalizer",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GreenCyan
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle
            Text(
                text = "Coming Soon",
                fontSize = 20.sp,
                color = GreenCyan.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Feature list card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MediumIndigoPurple.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Planned Features:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenCyan,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    val features = listOf(
                        "• 10-band graphic equalizer",
                        "• Custom presets",
                        "• Bass boost & virtualization",
                        "• Real-time spectrum analyzer",
                        "• Auto-EQ optimization"
                    )
                    
                    features.forEach { feature ->
                        Text(
                            text = feature,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Phase 5 note
            Text(
                text = "Available in Phase 5",
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = 0.5f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}