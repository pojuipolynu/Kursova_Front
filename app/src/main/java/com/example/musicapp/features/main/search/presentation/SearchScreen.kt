package com.example.musicapp.features.main.search.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.main.BottomTrackBar
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.ui.theme.Black80
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.Red60
import com.example.musicapp.ui.theme.White80


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    likedTracksViewModel: LikedTracksViewModel = hiltViewModel(),
    onTrackClick: (String) -> Unit,
    userId: String,
) {
    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()
    val searchQuery by likedTracksViewModel.searchQuery.collectAsState()
    val filteredTracks by likedTracksViewModel.filteredTracks.collectAsState()


    LaunchedEffect(Unit) {
        likedTracksViewModel.loadTracks()
        likedTracksViewModel.loadLikedTrackIds(userId)
        likedTracksViewModel.setCurrentSourcePage("Search")
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
            HeaderComponent(text = "Пошук")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    likedTracksViewModel.updateSearchQuery(query)
                    likedTracksViewModel.searchTracks(query)
                },
                placeholder = {
                    Text(
                        text = "Пошук",
                        color = White80,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = White80,
                    focusedBorderColor = Red60,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black80, RoundedCornerShape(4.dp)),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = White80,
                        modifier = Modifier.clickable { likedTracksViewModel.filterTracks() }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTracks) { track ->
                    val isLiked = likedTracksState.likedTrackIds.contains(track.id.toString()) // Check if track is liked
                    TrackRow(
                        track = track,
                        isLiked = isLiked,
                        onLikeClick = { likedTracksViewModel.toggleLike(userId, track.id) },
                        onTrackClick = {
                            likedTracksViewModel.playTrack(track, "Search")
                            onTrackClick(track.id)
                        }

                    )
                }
            }
        }
    }
}


@Composable
fun TrackRow(
    track: Track,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onTrackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = track.imageUrl),
            contentDescription = "Track Image",
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .padding(end = 8.dp),
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title,
                color = White80,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = track.artist,
                color = White80,
                style = MaterialTheme.typography.labelSmall
            )
        }
        IconButton(
            onClick = onLikeClick
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = if (isLiked) Color.Red else Color.Gray
            )
        }
    }
}
