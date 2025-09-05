package com.ftl.audioplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Indigo & Cyan theme
private val DarkColorScheme = darkColorScheme(
    primary = DeepPurple,
    secondary = CyberPink,
    tertiary = Cyan,
    background = DarkIndigoPurple,
    surface = MediumIndigoPurple,
    surfaceVariant = LightIndigoPurple,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = DarkIndigoPurple,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun FTLAudioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}