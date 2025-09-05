package com.ftl.audioplayer.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ftl.audioplayer.ui.components.MiniPlayer
import com.ftl.audioplayer.ui.screens.LibraryScreen
import com.ftl.audioplayer.ui.screens.NowPlayingScreen
import com.ftl.audioplayer.ui.screens.PlaylistsScreen
import com.ftl.audioplayer.ui.screens.SettingsScreen
import com.ftl.audioplayer.ui.screens.EqualizerScreen
import com.ftl.audioplayer.ui.theme.GreenCyan
import com.ftl.audioplayer.ui.theme.DarkIndigoPurple
import com.ftl.audioplayer.ui.theme.MediumIndigoPurple

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
) {
    object Library : Screen("library", "Library", Icons.Default.Home, Icons.Filled.Home)
    object Playlists : Screen("playlists", "Playlists", Icons.Default.List, Icons.Filled.List)
    object Equalizer : Screen("equalizer", "EQ", Icons.Default.Star, Icons.Filled.Star)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings, Icons.Filled.Settings)
    object NowPlaying : Screen("now_playing", "Now Playing", Icons.Default.PlayArrow, Icons.Filled.PlayArrow)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FTLNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomNavItems = listOf(
        Screen.Library,
        Screen.Playlists,
        Screen.Equalizer,
        Screen.Settings
    )
    
    // Hide bottom bar for Now Playing screen
    val showBottomBar = currentDestination?.route != Screen.NowPlaying.route
    
    Scaffold(
        bottomBar = @Composable {
            if (showBottomBar) {
                Column {
                    // Mini Player
                    MiniPlayer(
                        onExpandClick = {
                            navController.navigate(Screen.NowPlaying.route)
                        }
                    )
                    
                    // Bottom Navigation
                    NavigationBar(
                        containerColor = MediumIndigoPurple.copy(alpha = 0.95f),
                        contentColor = GreenCyan,
                        modifier = Modifier.height(80.dp)
                    ) {
                        bottomNavItems.forEach { screen ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon else screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) GreenCyan else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        color = if (isSelected) GreenCyan else Color.Gray,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = GreenCyan,
                                    selectedTextColor = GreenCyan,
                                    unselectedIconColor = Color.Gray,
                                    unselectedTextColor = Color.Gray,
                                    indicatorColor = GreenCyan.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Library.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Library.route) {
                LibraryScreen(
                    onNavigateToNowPlaying = {
                        navController.navigate(Screen.NowPlaying.route)
                    }
                )
            }
            composable(Screen.Playlists.route) {
                PlaylistsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(Screen.Equalizer.route) {
                EqualizerScreen()
            }
            composable(Screen.NowPlaying.route) {
                NowPlayingScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}