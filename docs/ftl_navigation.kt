package com.subcoder.ftlhiresaudio.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.subcoder.ftlhiresaudio.presentation.screens.home.HomeScreen
import com.subcoder.ftlhiresaudio.presentation.screens.library.LibraryScreen
import com.subcoder.ftlhiresaudio.presentation.screens.equalizer.EqualizerScreen
import com.subcoder.ftlhiresaudio.presentation.screens.settings.SettingsScreen
import com.subcoder.ftlhiresaudio.presentation.screens.nowplaying.NowPlayingScreen
import com.subcoder.ftlhiresaudio.presentation.components.FTLBottomNavigation
import com.subcoder.ftlhiresaudio.presentation.components.FTLMiniPlayer

/**
 * FTL Navigation Component
 * 
 * Main navigation structure for the FTL Audio Player:
 * - Home (Dashboard with visualizer)
 * - Library (Music collection)
 * - Equalizer (32-band EQ)
 * - Settings (App configuration)
 * - Now Playing (Full-screen player)
 */

@Composable
fun FTLNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        containerColor = com.subcoder.ftlhiresaudio.presentation.theme.FTLColors.background,
        bottomBar = {
            FTLBottomNavigation(
                navController = navController
            )
        }
    ) { paddingValues ->
        // Mini player overlay
        FTLMiniPlayer(
            modifier = Modifier.padding(paddingValues),
            onClick = {
                navController.navigate(FTLScreen.NowPlaying.route)
            }
        )
        
        NavHost(
            navController = navController,
            startDestination = FTLScreen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(FTLScreen.Home.route) {
                HomeScreen(navController = navController)
            }
            
            composable(FTLScreen.Library.route) {
                LibraryScreen(navController = navController)
            }
            
            composable(FTLScreen.Equalizer.route) {
                EqualizerScreen(navController = navController)
            }
            
            composable(FTLScreen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            
            composable(FTLScreen.NowPlaying.route) {
                NowPlayingScreen(
                    navController = navController,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Screen definitions for FTL Audio Player
 */
sealed class FTLScreen(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : FTLScreen(
        route = "home",
        title = "Command Center",
        icon = androidx.compose.material.icons.Icons.Default.Dashboard
    )
    
    object Library : FTLScreen(
        route = "library",
        title = "Audio Archive",
        icon = androidx.compose.material.icons.Icons.Default.LibraryMusic
    )
    
    object Equalizer : FTLScreen(
        route = "equalizer", 
        title = "Audio Matrix",
        icon = androidx.compose.material.icons.Icons.Default.Equalizer
    )
    
    object Settings : FTLScreen(
        route = "settings",
        title = "System Config",
        icon = androidx.compose.material.icons.Icons.Default.Settings
    )
    
    object NowPlaying : FTLScreen(
        route = "nowplaying",
        title = "Audio Bridge",
        icon = androidx.compose.material.icons.Icons.Default.MusicNote
    )
}

/**
 * Navigation helper extensions
 */
fun androidx.navigation.NavController.navigateToScreen(screen: FTLScreen) {
    navigate(screen.route) {
        // Avoid multiple copies of the same destination when reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

/**
 * Get all bottom navigation screens
 */
fun getBottomNavigationScreens(): List<FTLScreen> {
    return listOf(
        FTLScreen.Home,
        FTLScreen.Library,
        FTLScreen.Equalizer,
        FTLScreen.Settings
    )
}