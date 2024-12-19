package com.example.musicapp.features.main


import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.musicapp.features.main.likedtracks.presentation.TrackPlayerScreen
import com.example.musicapp.features.main.profile.presentation.ProfileScreen
import com.example.musicapp.features.main.search.presentation.SearchScreen
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.White80

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

    Scaffold(
        bottomBar = {
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
                            ) },
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
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Search.route,
            modifier = Modifier.padding(innerPadding),

        ) {
            composable(BottomNavItem.Search.route) {
                SearchScreen()
            }
            composable(BottomNavItem.Favourite.route) {
                FavouriteScreen(
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
                val track = likedTracksViewModel.getTrackById(trackId ?: "")
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
    data object Favourite : BottomNavItem("favourite","вподобане", Icons.Filled.Favorite)
    data object Profile : BottomNavItem("profile", "профіль", Icons.Filled.Person)
}
