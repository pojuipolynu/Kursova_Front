package com.example.musicapp.features.main.artists.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.main.artists.data.AlbumResult
import com.example.musicapp.features.main.artists.data.ArtistResult
import com.example.musicapp.features.main.artists.domain.ArtistViewModel
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.search.presentation.TrackRow
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.White80

@Composable
fun AlbumScreen(
    likedTracksViewModel: LikedTracksViewModel,
    artistViewModel: ArtistViewModel,
    albumId: String,
    userId: String,
    onBackClick: () -> Unit,
    onTrackClick: (String) -> Unit
) {
    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()
    val albumState by artistViewModel.albumState.collectAsState()
    val albumDetails by artistViewModel.albumDetails.collectAsState()
    val albumTracks by artistViewModel.albumTracks.collectAsState()

    likedTracksViewModel.setCurrentAlbumTracks(albumTracks)

    LaunchedEffect(albumId) {
        artistViewModel.fetchAlbumTracks(albumId)
        artistViewModel.fetchAlbumDetails(albumId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Black90)
            .padding(16.dp),
    ) {
        when (val result = albumState) {
            is AlbumResult.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is AlbumResult.Success -> {
                item {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = White80
                                )
                            }
                            HeaderComponent(text = albumDetails?.title ?: "")
                        }
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }

                items(albumTracks) { track ->
                    TrackRow(
                        track = track,
                        isLiked = likedTracksState.likedTrackIds.contains(track.id.toString()),
                        onLikeClick = { likedTracksViewModel.toggleLike(userId, track.id) },
                        onTrackClick = {
                            likedTracksViewModel.setCurrentSourcePage("Album")
                            likedTracksViewModel.playTrack(track, "Album")
                            onTrackClick(track.id)
                        }
                    )
                }
            }
            is AlbumResult.Error -> {
                item {
                    Text(
                        text = "Error: ${result.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}