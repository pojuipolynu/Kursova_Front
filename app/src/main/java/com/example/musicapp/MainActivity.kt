package com.example.musicapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.musicapp.features.auth.presentation.LoginScreen
import com.example.musicapp.features.main.MainAppScreen
import com.example.musicapp.ui.theme.MusicAppTheme
import com.example.musicapp.features.auth.domain.AuthViewModel
import com.example.musicapp.features.main.artists.domain.ArtistViewModel
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.player.presentation.PlayerScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val likedTracksViewModel: LikedTracksViewModel = hiltViewModel()
                    val artistViewModel: ArtistViewModel = hiltViewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "main_graph"
//                        startDestination = if (authViewModel.isUserLoggedIn()) "main_graph" else "auth_graph"
                    ) {
                        authGraph(navController, authViewModel)
                        mainGraph(navController, authViewModel, likedTracksViewModel, artistViewModel)
                    }
                }
            }
        }
    }
}


fun NavGraphBuilder.authGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    navigation(startDestination = "login", route = "auth_graph") {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                activity = navController.context as MainActivity,
                onLoginSuccess = {
                    authViewModel.viewModelScope.launch {
                        authViewModel.saveUserStatus()
                    }
                    navController.navigate("main_graph") {
                        popUpTo("auth_graph") { inclusive = true }
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.mainGraph(navController: NavHostController, authViewModel: AuthViewModel, likedTracksViewModel: LikedTracksViewModel, artistViewModel: ArtistViewModel) {
    navigation(startDestination = "main", route = "main_graph") {
        composable("main") { MainAppScreen(
            authViewModel = authViewModel,
            likedTracksViewModel = likedTracksViewModel,
            navController = navController,
            artistViewModel = artistViewModel
        ) }

        composable("player_screen") {
            val currentTrack by likedTracksViewModel.currentTrack.collectAsState()
            val currentPosition by likedTracksViewModel.currentPosition.collectAsState()
            val isPlaying by likedTracksViewModel.isPlaying.collectAsState()

            LaunchedEffect(currentTrack) {
                Log.d("MainScreen", "Current track changed: ${currentTrack?.id}")
            }

            Log.d("PlayerScreen", "Current track: $currentTrack")
            Log.d("PlayerScreen", "Is playing: $isPlaying")


            currentTrack?.let { track ->
                PlayerScreen(
                    likedTracksViewModel = likedTracksViewModel,
                    userId = authViewModel.getCurrentUserId() ?: "1",
                    track = track,
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    onPlayClick = {
                        likedTracksViewModel.togglePlayPause(likedTracksViewModel.currentSourcePage.value ?: "Unknown")
                    },
                    onTrackClick = {
                        likedTracksViewModel.playTrack(track, likedTracksViewModel.currentSourcePage.value ?: "Unknown")
                    },
                    onNextClick = { likedTracksViewModel.skipToNextTrack() },
                    onPreviousClick = { likedTracksViewModel.skipToPreviousTrack() },
                    onSeekTo = { newPosition -> likedTracksViewModel.seekTo(newPosition.toInt()) },
                    onArtistClick = { artistId ->
                        Log.d("PlayerScreen", "Artist clicked: $artistId")
                        navController.previousBackStackEntry?.savedStateHandle?.set("artist_id", artistId)
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.previousBackStackEntry?.savedStateHandle?.remove<String>("artist_id")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}


