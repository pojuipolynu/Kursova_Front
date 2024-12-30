package com.example.musicapp.features.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicapp.features.auth.domain.AuthViewModel
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.likedtracks.presentation.FavouriteScreen
import com.example.musicapp.features.main.profile.presentation.ProfileScreen
import com.example.musicapp.features.main.search.presentation.SearchScreen
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.White80

import androidx.compose.runtime.collectAsState // Імпорт для collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.features.main.likedtracks.data.Track

import com.example.musicapp.features.main.playlists.presentation.CreatePlaylistScreen
import com.example.musicapp.features.main.playlists.presentation.ViewPlaylistScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@Composable
fun MainAppScreen(
    navController: NavController,
) {
    val bottomNavController = rememberNavController()
    val likedTracksViewModel: LikedTracksViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()

    val bottomNavItems = listOf(
        BottomNavItem.Search,
        BottomNavItem.Favourite,
        BottomNavItem.Profile
    )

    val currentTrack by likedTracksViewModel.currentTrack.collectAsState()
    val isPlaying by likedTracksViewModel.isPlaying.collectAsState()

    Scaffold(
        bottomBar = {
            Column {

                currentTrack?.let { track ->
                    BottomTrackBar(
                        track = track,
                        isPlaying = isPlaying,
                        onPlayClick = {
                            likedTracksViewModel.togglePlayPause()
                        },
                        onTrackClick = {
                            likedTracksViewModel.playTrack(track)
                        }
                    )
                }


                NavigationBar(
                    containerColor = Black90,
                ) {
                    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors(
                                selectedTextColor = White80,
                                unselectedTextColor = White80.copy(alpha = 0.6f),
                                selectedIconColor = White80,
                                unselectedIconColor = White80.copy(alpha = 0.6f),
                                indicatorColor = Black90,
                            ),
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.route
                                )
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == screen.route
                            } == true,
                            onClick = {
                                bottomNavController.navigate(screen.route) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }

                currentTrack?.let { track ->
                    BottomTrackBar(
                        track = track,
                        isPlaying = isPlaying,
                        onPlayClick = {
                            likedTracksViewModel.togglePlayPause(likedTracksViewModel.currentSourcePage.value ?: "Unknown")
                        },
                        onTrackClick = {
                            likedTracksViewModel.playTrack(track, likedTracksViewModel.currentSourcePage.value ?: "Unknown")
                        },
                        onNextClick = {
                            likedTracksViewModel.skipToNextTrack()
                        },
                        onPreviousClick = {
                            likedTracksViewModel.skipToPreviousTrack()
                        }
                    )
                }



            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Search.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(BottomNavItem.Search.route) {
                SearchScreen(
                    userId = authViewModel.getCurrentUserId() ?: "1",
                    onTrackClick = { trackId ->
                        val track = likedTracksViewModel.getTrackByIdSync(trackId)
                        track?.let { likedTracksViewModel.playTrack(it, "Search") }
                    }
                )
            }
            composable(BottomNavItem.Favourite.route) {
                FavouriteScreen(
                    userId = authViewModel.getCurrentUserId() ?: "1",
                    onTrackClick = { trackId ->
                        val track = likedTracksViewModel.getTrackByIdSync(trackId)
                        track?.let { likedTracksViewModel.playTrack(it, "Favourite") }
                    }
                )
            }

            composable(BottomNavItem.Profile.route) {
                val profileNavController = rememberNavController()
                NavHost(
                    navController = profileNavController,
                    startDestination = "profile_screen",
                ) {
                    composable("profile_screen") {
                        ProfileScreen(
                            onLogout = {
                                authViewModel.signOut()
                                navController.navigate("auth_graph") {
                                    popUpTo("main_graph") { inclusive = true }
                                }
                            },
                            onNavigateToCreatePlaylist = { profileNavController.navigate("create_playlist") },
                            onNavigateToViewPlaylist = { playlistId ->
                                profileNavController.navigate("view_playlist/$playlistId")
                            }
                        )
                    }

                    composable("create_playlist") {
                        CreatePlaylistScreen(
                            onNavigateBack = { profileNavController.popBackStack() },
                            onNavigateToPlaylist = { playlistId ->
                                profileNavController.navigate("view_playlist/$playlistId") {
                                    popUpTo("profile_screen") { inclusive = false }
                                }
                            }
                        )
                    }

                    composable("view_playlist/{playlistId}") { backStackEntry ->
                        val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
                        ViewPlaylistScreen(
                            playlistId = playlistId,
                            onNavigateBack = { profileNavController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}

// Sealed class to define bottom navigation items
sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Search : BottomNavItem("search", "пошук", Icons.Filled.Search)
    data object Favourite : BottomNavItem("favourite", "вподобане", Icons.Filled.Favorite)
    data object Profile : BottomNavItem("profile", "профіль", Icons.Filled.Person)
}
@Composable
fun BottomTrackBar(
    track: Track,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onTrackClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(16.dp)
            .clickable { onTrackClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = track.imageUrl),
            contentDescription = "Track Image",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = track.artist,
                color = Color.White,
                fontSize = 14.sp
            )
        }
        Row {
            IconButton(onClick = onPreviousClick) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Track",
                    tint = Color.White
                )
            }
            IconButton(onClick = onPlayClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Outlined.PlayArrow else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause Icon" else "Play Icon",
                    tint = Color.White)
            }
            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Next Track",
                    tint = Color.White
                )
            }
        }
    }
}
