package com.example.musicapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.likedtracks.presentation.FavouriteScreen
import com.example.musicapp.features.main.playlists.presentation.CreatePlaylistScreen
import com.example.musicapp.features.main.playlists.presentation.ViewPlaylistScreen
import com.example.musicapp.features.main.profile.presentation.ProfileScreen
import com.example.musicapp.features.main.search.presentation.SearchScreen
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

                    NavHost(
                        navController = navController,
                        startDestination = "main_graph"
//                        startDestination = if (authViewModel.isUserLoggedIn()) "main_graph" else "auth_graph"
                    ) {
                        authGraph(navController, authViewModel)
                        mainGraph(navController, authViewModel, likedTracksViewModel)
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

fun NavGraphBuilder.mainGraph(navController: NavHostController, authViewModel: AuthViewModel, likedTracksViewModel: LikedTracksViewModel) {
    navigation(startDestination = "main", route = "main_graph") {
        composable("main") { MainAppScreen(
            navController = navController,
        ) }
    }
}


