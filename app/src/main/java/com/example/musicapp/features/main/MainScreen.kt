package com.example.musicapp.features.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
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
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.main.likedtracks.data.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@Composable
fun MainAppScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val likedTracksViewModel: LikedTracksViewModel = hiltViewModel()

    // Define bottom navigation items
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
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
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
                            likedTracksViewModel.togglePlayPause()
                        },
                        onTrackClick = {
                            bottomNavController.navigate("trackDetails/${track.id}")
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
                    userId = "1",
                    onTrackClick = { trackId ->
                        Log.d("SearchScreen", "Track ID: $trackId")
                        bottomNavController.navigate("trackDetails/$trackId")
                    }
                )
            }
            composable(BottomNavItem.Favourite.route) {
                FavouriteScreen(
                    userId = "1",
                    onTrackClick = { trackId ->
                        Log.d("FavouriteScreen", "Track ID: $trackId")
                        bottomNavController.navigate("trackDetails/$trackId")
                    }
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }


            composable(
                "trackDetails/{trackId}",
                arguments = listOf(navArgument("trackId") { type = NavType.StringType })
            ) { backStackEntry ->
                val trackId = backStackEntry.arguments?.getString("trackId")
                val track = likedTracksViewModel.getTrackByIdSync(trackId ?: "")
                if (track != null) {
                    TrackPlayerScreen(track = track)
                } else {
                    Text("Track not found")
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
    onTrackClick: () -> Unit
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
        IconButton(onClick = onPlayClick) {
            Icon(
                imageVector = if (isPlaying) Icons.Outlined.PlayArrow else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause Icon" else "Play Icon",
                tint = Color.White
            )
        }
    }
}


@Composable
fun TrackPlayerScreen(
    track: Track,
    likedTracksViewModel: LikedTracksViewModel = hiltViewModel()
) {
    val currentTrack by likedTracksViewModel.currentTrack.collectAsState()
    val isPlaying by likedTracksViewModel.isPlaying.collectAsState()
    val currentPosition by likedTracksViewModel.currentPosition.collectAsState()

    LaunchedEffect(currentTrack) {
        while (isActive) { // Перевіряємо, чи не завершено LaunchedEffect
            likedTracksViewModel.updateCurrentPosition()
            delay(1000) // Оновлюємо кожну секунду
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderComponent(text = " ")
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(model = track.imageUrl),
                contentDescription = "Track Image",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = track.title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = track.artist,
                fontSize = 18.sp,
                color = Color.White
            )
            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { newPosition ->
                    likedTracksViewModel.seekTo(newPosition.toInt())
                },

                valueRange = 0f..(likedTracksViewModel.getDuration().toFloat()),
                colors = SliderDefaults.colors(Color.Red, Color.Red)
            )
            Button(
                modifier = Modifier.height(70.dp).width(70.dp),
                colors =  ButtonDefaults.buttonColors(Color.Red),
                onClick = {
                likedTracksViewModel.togglePlayPause()

            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Outlined.PlayArrow else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause Icon" else "Play Icon",
                    tint = Color.White,
                    modifier = Modifier.height(42.dp)
                )
            }
        }
    }
}

