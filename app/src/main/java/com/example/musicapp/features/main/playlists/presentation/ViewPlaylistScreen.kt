package com.example.musicapp.features.main.playlists.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.auth.domain.AuthViewModel
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.playlists.domain.PlaylistViewModel
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.White80
import kotlinx.coroutines.launch
import com.example.musicapp.features.main.search.presentation.TrackRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPlaylistScreen(
    likedTracksViewModel: LikedTracksViewModel,
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    playlistId: String,
    onNavigateBack: () -> Unit,
    onTrackClick: (String) -> Unit,
) {
    val playlistTracks by playlistViewModel.getPlaylistTracks(playlistId)
        .collectAsState(initial = emptyList())
    val allTracks by playlistViewModel.allTracks.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val playlistName = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        authViewModel.getCurrentUserId()?.let { userId ->
            scope.launch {
                playlistName.value = playlistViewModel.getPlaylistName(userId, playlistId)
            }
        }
    }

    val editingMode = remember { mutableStateOf(false) }

    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()

    LaunchedEffect(Unit) {
        //МОК ТРЕБА ЗАМІНИТИ
        likedTracksViewModel.loadFavourites("1")
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
            HeaderComponent(text = playlistName.value)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (editingMode.value) {
                    items(playlistTracks.size) { index ->
                        val track = playlistTracks[index]
                        val isLiked =
                            likedTracksState.likedTrackIds.contains(track.id.toString()) // Check if track is liked
                        TrackRow(
                            track = track,
                            isLiked = isLiked,
                            onLikeClick = { likedTracksViewModel.toggleLike("1", track.id) },
                            onTrackClick = {
                                likedTracksViewModel.playTrack(track, "Playlist")
                                onTrackClick(track.id)
                            }

                        )
                    }
                } else {
                    items(playlistTracks.size) { index ->
                        val track = playlistTracks[index]
                        val isLiked =
                            likedTracksState.likedTrackIds.contains(track.id.toString())
                        TrackRow(
                            track = track,
                            isLiked = isLiked,
                            onLikeClick = { likedTracksViewModel.toggleLike("1", track.id) },
                            onTrackClick = {
                                likedTracksViewModel.playTrack(track, "Playlist")
                                onTrackClick(track.id)
                            }

                        )
                    }
                }
            }
        }

    }
}
