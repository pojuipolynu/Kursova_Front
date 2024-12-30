package com.example.musicapp.features.main.likedtracks.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.search.presentation.TrackRow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    likedTracksViewModel: LikedTracksViewModel = hiltViewModel(),
    userId: String,
    onTrackClick: (String) -> Unit
) {
    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("FavouriteScreen", "Loading favourites")
        likedTracksViewModel.loadFavourites(userId)
        likedTracksViewModel.setCurrentSourcePage("Favourite")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HeaderComponent(text = "Ваша медіатека")

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(likedTracksState.tracks) { track ->
                    TrackRow(
                        track = track,
                        isLiked = likedTracksState.likedTrackIds.contains(track.id.toString()),
                        onLikeClick = { likedTracksViewModel.toggleLike(userId, track.id) },
                        onTrackClick = {
                            likedTracksViewModel.playTrack(
                                track,
                                "Favourite"
                            )
                            onTrackClick(track.id)
                        }
                    )
                }
            }
        }

    }
}


