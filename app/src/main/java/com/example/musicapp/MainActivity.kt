package com.example.musicapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.musicapp.features.auth.presentation.LoginScreen
import com.example.musicapp.features.main.MainAppScreen
import com.example.musicapp.ui.theme.MusicAppTheme
import com.example.musicapp.features.auth.domain.AuthViewModel
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.likedtracks.presentation.FavouriteScreen
import com.example.musicapp.features.main.likedtracks.presentation.TrackPlayerScreen
import com.example.musicapp.features.main.profile.presentation.ProfileScreen
import com.example.musicapp.features.main.search.presentation.SearchScreen
import dagger.hilt.android.AndroidEntryPoint

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

//                    CoroutineScope(Dispatchers.IO).launch {
//                        try {
//                            // Отримуємо дані з API
//                            val post = RetrofitInstance.apiService.getPost()
//
//                            // Виводимо результат у консоль
//                            withContext(Dispatchers.Main) {
//                                Log.d("API Response", "Title: ${post.title}")
//                                Log.d("API Response", "Body: ${post.body}")
//                            }
//                        } catch (e: Exception) {
//                            withContext(Dispatchers.Main) {
//                                Log.e("API Error", "Error fetching data: ${e.message}")
//                            }
//                        }
//                    }

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

//            MusicAppTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    val navController = rememberNavController()
//                    val authViewModel: AuthViewModel = hiltViewModel()
//
//                    // Determine initial route based on login state
//                    var startDestination by remember {
//                        mutableStateOf(
//                            if (authViewModel.isUserLoggedIn()) "main" else "login"
//                        )
//                    }
//
//                    NavHost(navController = navController, startDestination = startDestination) {
//                        composable("login") {
//                            LoginScreen(
//                                authViewModel = authViewModel,
//                                activity = this@MainActivity,
//                                onLoginSuccess = {
//                                    navController.navigate("main") {
//                                        popUpTo("login") { inclusive = true }
//                                    }
//                                }
//                            )
//                        }
//                        composable("main") {
//                            MainAppScreen(
//                                authViewModel = authViewModel,
//                                onLogout = {
//                                    navController.navigate("login") {
//                                        popUpTo("main") { inclusive = true }
//                                    }
//                                }
//                            )
//                        }
//                    }
//                }
//            }
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
        composable("main") {
            MainAppScreen(
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate("auth_graph") {
                        popUpTo("main_graph") { inclusive = true }
                    }
                }
            )
        }
        composable("profile") { ProfileScreen() }
        composable("search") { SearchScreen() }
        composable("favourite") {
            FavouriteScreen(
                onTrackClick = { trackId ->
                    Log.d("FavouriteScreen", "Track ID: $trackId")
                    navController.navigate("trackDetails/$trackId")
                }
            )
        }
        composable(
            "trackDetails/{trackId}",
            arguments = listOf(navArgument("trackId") { type = NavType.StringType })
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId")
            val track = likedTracksViewModel.getTrackById(trackId ?: "")
            if (track != null) {
                TrackPlayerScreen(track = track)
            } else {
                Text("Track not found")
            }
        }
    }
}