package com.example.musicapp.features.main.search.presentation

import androidx.compose.foundation.Image
import androidx.compose.ui.unit.sp
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
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.ui.theme.Black80
import com.example.musicapp.ui.theme.Red60
import com.example.musicapp.ui.theme.White80

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.border
import com.example.musicapp.features.main.album.data.Album
import com.example.musicapp.features.main.artist.data.Artist

//
//import androidx.compose.foundation.layout.weight
//import androidx.compose.ui.graphics.BorderStroke



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    likedTracksViewModel: LikedTracksViewModel,
    onTrackClick: (String) -> Unit,
    userId: String,
) {

    val selectedCategory = remember { mutableStateOf("songs") }

    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()
    val searchQuery by likedTracksViewModel.searchQuery.collectAsState()
    val filteredTracks by likedTracksViewModel.filteredTracks.collectAsState()
    val filteredArtist by likedTracksViewModel.artistSearchResults.collectAsState()
    val filteredAlbum by likedTracksViewModel.albumSearchResults.collectAsState()

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
                    likedTracksViewModel.updateSearchQuery(query, selectedCategory.value) // Передаємо категорію
                    when (selectedCategory.value) {
                        "songs" -> likedTracksViewModel.searchTracks(query)
                        "albums" -> likedTracksViewModel.searchAlbums(query)
                        "artists" -> likedTracksViewModel.searchArtists(query)
                    }
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
                        modifier = Modifier.clickable { likedTracksViewModel.filterTracks(selectedCategory.value) }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton("Пісні",
                    "songs",
                    selectedCategory.value,
                    { selectedCategory.value = it },
//                    likedTracksViewModel,
//                    userId
                )
                ActionButton("Альбоми",
                    "albums",selectedCategory.value,
                    { selectedCategory.value = it },
//                    likedTracksViewModel,
//                    userId
                )
                ActionButton("Виконавці",
                    "artists",
                    selectedCategory.value,
                    { selectedCategory.value = it },
//                    likedTracksViewModel,
//                    userId
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                when (selectedCategory.value) {
                    "songs" -> {
                        items(filteredTracks) { track ->
                            val isLiked = likedTracksState.likedTrackIds.contains(track.id.toString())
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
                    "artists" -> {
                        items(filteredArtist) { artist ->
                            ArtistRow(
                                artist = artist,
                                onArtistClick = {
                                    // Логіка для взаємодії з виконавцем
                                    println("Clicked artist: ${artist.name}")
                                }
                            )
                        }
                    }
                    "albums" -> {
                        items(filteredAlbum) { album ->
                            AlbumRow(
                                album = album,
                                onAlbumClick = {
                                    // Логіка для взаємодії з альбомом
                                    println("Clicked album: ${album.title}")
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}

//fun handleCategoryAction(
//    category: String,
//    likedTracksViewModel: LikedTracksViewModel,
//    userId: String
//) {
//    when (category) {
//        "songs" -> {
//            likedTracksViewModel.loadTracks()
//            likedTracksViewModel.loadLikedTrackIds(userId)
//        }
//        "albums" -> {
//            likedTracksViewModel.loadAlbums()
//        }
//        "artists" -> {
//            likedTracksViewModel.loadArtists()
//        }
//    }
//}


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
                text = track.artist_id.toString(),
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

@Composable
fun ActionButton(
    text: String,
    category: String,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
//    likedTracksViewModel: LikedTracksViewModel,
//    userId: String
) {
    val isSelected = category == selectedCategory
    Button(
        onClick = { onCategorySelected(category)
//            handleCategoryAction(category, likedTracksViewModel, userId)
            },
        colors = if (isSelected)
                ButtonDefaults.buttonColors(containerColor = Color(0xFF9C3E3E))
            else
                ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier
//            .width(200.dp)
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}


@Composable
fun AlbumRow(
    album: Album,
    onAlbumClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAlbumClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = "https://music-app-file.s3.eu-central-003.backblazeb2.com/covers/a3896979361_65.jpg"),
//            painter = rememberAsyncImagePainter(model = album.imageUrl),
            contentDescription = "Album Image",
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
                text = album.title,
                color = White80,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = album.artist_id.toString(),
                color = White80,
                style = MaterialTheme.typography.labelSmall
            )
        }
        }
    }



@Composable
fun ArtistRow(
    artist: Artist,
    onArtistClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onArtistClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = artist.imageUrl),
            contentDescription = "Album Image",
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
                text = artist.name,
                color = White80,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

