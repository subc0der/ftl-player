package com.ftl.audioplayer.ui.theme

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                    FTL CYBERPUNK THEME                       ║
 * ║           Neural-Enhanced UI Design System                   ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * Cyberpunk aesthetic theme for the FTL Hi-Res Audio Player
 * 
 * Design Philosophy:
 * - Dark-first design with neural network inspired elements
 * - Fluorescent aquamarine accents (#00FFFF) for energy
 * - Deep indigo highlights (#4B0082) for elegance
 * - Sharp geometric shapes with glowing effects
 * - 120fps animation targets with smooth transitions
 */

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// CYBERPUNK COLOR PALETTE
// ═══════════════════════════════════════════════════════════════

val CyberAqua = Color(0xFF00FFFF)      // Fluorescent Aquamarine - Primary
val CyberIndigo = Color(0xFF4B0082)     // Deep Indigo - Secondary  
val CyberBlack = Color(0xFF000000)      // Pure Black - Background
val CyberDarkGray = Color(0xFF0A0A0A)   // Dark Gray - Surface
val CyberGray = Color(0xFF1A1A1A)       // Medium Gray - Surface Variant

// Neural Network Visualization Colors
val NeuralBlue = Color(0xFF0066FF)      // Neural connections
val NeuralPurple = Color(0xFF6600FF)    // Neural nodes
val NeuralGreen = Color(0xFF00FF66)     // Neural activity

// Audio Visualization Colors  
val WaveformCyan = Color(0xFF00CCFF)    // Waveform display
val SpectrumMagenta = Color(0xFFFF00FF) // Spectrum analyzer
val EQGlow = Color(0xFF66FFCC)          // EQ band highlights

/**
 * FTL Cyberpunk Dark Theme
 * Primary color scheme for neural-enhanced audio experience
 */
val FtlCyberpunkColorScheme = darkColorScheme(
    primary = CyberAqua,
    onPrimary = CyberBlack,
    primaryContainer = CyberIndigo,
    onPrimaryContainer = CyberAqua,
    
    secondary = CyberIndigo,
    onSecondary = CyberAqua,
    secondaryContainer = CyberDarkGray,
    onSecondaryContainer = CyberAqua,
    
    tertiary = NeuralGreen,
    onTertiary = CyberBlack,
    tertiaryContainer = CyberDarkGray,
    onTertiaryContainer = NeuralGreen,
    
    background = CyberBlack,
    onBackground = CyberAqua,
    surface = CyberDarkGray,
    onSurface = CyberAqua,
    surfaceVariant = CyberGray,
    onSurfaceVariant = CyberAqua,
    
    error = Color(0xFFFF3366),
    onError = CyberBlack,
    errorContainer = Color(0xFF330011),
    onErrorContainer = Color(0xFFFF99BB),
    
    outline = CyberIndigo,
    outlineVariant = CyberGray
)

@Composable
fun FtlCyberpunkTheme(
    content: @Composable () -> Unit
) {
    // Theme implementation will be completed in UI development phase
    content()
}