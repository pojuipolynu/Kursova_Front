package com.example.musicapp.features.main

import android.util.Log
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
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.musicapp.features.auth.domain.AuthViewModel
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.likedtracks.presentation.FavouriteScreen
import com.example.musicapp.features.main.profile.presentation.ProfileScreen
import com.example.musicapp.features.main.search.presentation.SearchScreen
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.White80

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.features.main.artists.domain.ArtistViewModel
import com.example.musicapp.features.main.artists.presentation.AlbumScreen
import com.example.musicapp.features.main.artists.presentation.ArtistScreen
import com.example.musicapp.features.main.likedtracks.data.Track

import com.example.musicapp.features.main.playlists.presentation.CreatePlaylistScreen
import com.example.musicapp.features.main.playlists.presentation.ViewPlaylistScreen


@Composable
fun MainAppScreen(
    authViewModel: AuthViewModel,
    likedTracksViewModel: LikedTracksViewModel,
    artistViewModel: ArtistViewModel,
    navController: NavController,
) {
    val bottomNavController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem.Search,
        BottomNavItem.Favourite,
        BottomNavItem.Profile
    )

    val currentTrack by likedTracksViewModel.currentTrack.collectAsState()
    val isPlaying by likedTracksViewModel.isPlaying.collectAsState()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val artistId = savedStateHandle?.getStateFlow<String?>("artist_id", null)?.collectAsState()

    LaunchedEffect(artistId?.value) {
        Log.d("MainAppScreenLaunch", "Listening for artist navigation with ID: ${artistId?.value}")
        artistId?.value?.let { id ->
            Log.d("MainAppScreenLaunch", "Navigating to artist screen $id")
            bottomNavController.navigate("artist_screen/$id") {
                popUpTo(bottomNavController.graph.findStartDestination().id) {
                    saveState = true
                }
            }
            savedStateHandle.remove<String>("artist_id")
        }
    }

    Scaffold(
        bottomBar = {
            Column {
                currentTrack?.let { track ->
                    BottomTrackBar(
                        track = track,
                        isPlaying = isPlaying,
                        onPlayClick = {
                            likedTracksViewModel.togglePlayPause(likedTracksViewModel.currentSourcePage.value ?: "Unknown")
                        },
                        onNavigateToPlayer = { navController.navigate("player_screen") },
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
                    likedTracksViewModel = likedTracksViewModel,
                    userId = authViewModel.getCurrentUserId() ?: "1",
                    onTrackClick = { trackId ->
                        val track = likedTracksViewModel.getTrackByIdSync(trackId)
                        track?.let { likedTracksViewModel.playTrack(it, "Search") }
                    }
                )
            }
            composable(BottomNavItem.Favourite.route) {
                FavouriteScreen(
                    likedTracksViewModel = likedTracksViewModel,
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
                            likedTracksViewModel = likedTracksViewModel,
                            onNavigateBack = { profileNavController.popBackStack() },
                        )
                    }

                    composable("view_playlist/{playlistId}") { backStackEntry ->
                        val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
                        ViewPlaylistScreen(
                            likedTracksViewModel = likedTracksViewModel,
                            playlistId = playlistId,
                            onNavigateBack = { profileNavController.popBackStack() },
                            onTrackClick = { trackId ->
                                val track = likedTracksViewModel.getTrackByIdSync(trackId)
                                track?.let { likedTracksViewModel.playTrack(it, "Playlist") }
                            }
                        )
                    }
                }
            }

            composable("artist_screen/{artistId}") { backStackEntry ->
                val artistId = backStackEntry.arguments?.getString("artistId") ?: return@composable
                ArtistScreen(
                    likedTracksViewModel = likedTracksViewModel,
                    artistViewModel = artistViewModel,
                    artistId = artistId,
                    userId = authViewModel.getCurrentUserId() ?: "1",
                    onNavigateToAlbum = { albumId ->
                        bottomNavController.navigate("album_screen/$albumId")
                    },
                    onBackClick = { bottomNavController.popBackStack() },
                    onTrackClick = { trackId ->
                        val track = likedTracksViewModel.getTrackByIdSync(trackId)
                        track?.let { likedTracksViewModel.playTrack(it, "Favourite") }
                    }
                )
            }

            composable("album_screen/{albumId}") { backStackEntry ->
                val albumId = backStackEntry.arguments?.getString("albumId") ?: return@composable
                AlbumScreen(
                    likedTracksViewModel = likedTracksViewModel,
                    artistViewModel = artistViewModel,
                    albumId = albumId,
                    userId = authViewModel.getCurrentUserId() ?: "1",
                    onBackClick = { bottomNavController.popBackStack() },
                    onTrackClick = { trackId ->
                        val track = likedTracksViewModel.getTrackByIdSync(trackId)
                        track?.let { likedTracksViewModel.playTrack(it, "Favourite") }
                    }
                )
            }
        }
    }
}


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
    onNavigateToPlayer: () -> Unit,
    onTrackClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(16.dp)
            .clickable {
                onNavigateToPlayer()
                onTrackClick()
                 },
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
                    imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Default.PlayArrow,
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
