package com.example.musicapp.features.main.artists.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.api.Album
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.main.artists.data.ArtistResult
import com.example.musicapp.features.main.artists.domain.ArtistViewModel
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.search.presentation.TrackRow
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.Red60
import com.example.musicapp.ui.theme.White80

@Composable
fun ArtistScreen(
    likedTracksViewModel: LikedTracksViewModel,
    artistViewModel: ArtistViewModel,
    artistId: String,
    userId: String,
    onNavigateToAlbum: (String) -> Unit,
    onBackClick: () -> Unit,
    onTrackClick: (String) -> Unit
) {
    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()

    val artistState by artistViewModel.artistState.collectAsState()
    val albums by artistViewModel.albums.collectAsState()
    val tracks by artistViewModel.tracks.collectAsState()

    LaunchedEffect(artistId) {
        artistViewModel.fetchArtistDetails(artistId)
        artistViewModel.fetchArtistAlbums(artistId)
        artistViewModel.fetchArtistTracks(artistId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Black90)
            .padding(16.dp),
    ) {

        when (val result = artistState) {
            is ArtistResult.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is ArtistResult.Success -> {
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
                            HeaderComponent(text = result.artist.name)
                        }
                        AsyncImage(
                            model = result.artist.imageUrl,
                            contentDescription = "Artist Image",
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.CenterHorizontally)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(Modifier.height(40.dp))
                    }
                }

                item {
                    Text(
                        "Альбоми",
                        Modifier.padding(bottom = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = White80)
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(albums) { album ->
                            val albumImageUrl = getFirstTrackImageUrl(album.id, tracks)

                            Log.d("ArtistScreen", "Album Image URL: $albumImageUrl")

                            AlbumItem(
                                album = album,
                                imageUrl = albumImageUrl!!,
                                onClick = { onNavigateToAlbum(album.id.toString()) }
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Треки",
                        Modifier.padding(top = 40.dp, bottom = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = White80)
                }
                items(tracks) { track ->
                    TrackRow(
                        track = track,
                        isLiked = likedTracksState.likedTrackIds.contains(track.id.toString()),
                        onLikeClick = { likedTracksViewModel.toggleLike(userId, track.id) },
                        onTrackClick = {
                            likedTracksViewModel.playTrack(
                                track,
                                "Favorite"
                            )
                            onTrackClick(track.id)
                        }
                    )
                }
            }
            is ArtistResult.Error -> {
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


@Composable
fun AlbumItem(
    album: Album,
    imageUrl: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = "Track Image",
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = album.title,
            style = MaterialTheme.typography.bodySmall,
            color = White80,
            textAlign = TextAlign.Left
        )
    }
}


fun getFirstTrackImageUrl(albumId: Int, tracks: List<Track>): String? {
    val firstTrack = tracks.firstOrNull { it.album_id == albumId }
    return firstTrack?.imageUrl
}


