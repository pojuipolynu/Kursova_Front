package com.example.musicapp.features.main.likedtracks.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp


import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.White80
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    likedTracksViewModel: LikedTracksViewModel = hiltViewModel(),
    onTrackClick: (String) -> Unit
) {
    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()
    var currentTrack by remember { mutableStateOf<Track?>(null) }

    LaunchedEffect(Unit) {
        likedTracksViewModel.loadLikedTracks()
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
            // Header
            HeaderComponent(text = "Ваша медіатека")

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(text = "Пошук", color = White80, style = MaterialTheme.typography.bodySmall)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray, RoundedCornerShape(4.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    backgroundColor = Color.Transparent,
                    cursorColor = Color.White,
                    unfocusedTextColor = White80,
                    unfocusedPrefixColor = White80,
//                    textColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = White80
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(text = "Відтворити", icon = Icons.Default.PlayArrow)
                ActionButton(text = "Тасувати", icon = Icons.Default.PlayArrow)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of tracks
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(likedTracksState.tracks) { track ->
                    TrackRow(
                        track = track,
                        isLiked = likedTracksState.likedTrackIds.contains(track.id),
                        onLikeClick = { likedTracksViewModel.toggleLike(track.id) },
                        onTrackClick = {
//                            onTrackClick(track.id)
                            currentTrack = track
                        }
                    )
                }
            }
        }

        // Bottom track bar
        currentTrack?.let { track ->
            BottomTrackBar(
                track = track,
                onPlayClick = { /* Handle play action */ },
                onTrackClick = { onTrackClick(track.id) },
            )
        }
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector) {
    Button(
        onClick = { /* Handle button click */ },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C3E3E)),
        modifier = Modifier
            .width(200.dp)
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun BottomTrackBar(track: Track, onPlayClick: () -> Unit, onTrackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(16.dp)
            .clickable { onTrackClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = track.imageUrl),
            contentDescription = "Track Image",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = track.artist,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        IconButton(onClick = onPlayClick) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Icon",
                tint = Color.White
            )
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


@Composable
fun TrackPlayerScreen(track: Track) {
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0f) }

    DisposableEffect(track.fileUrl) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.fileUrl)
            prepare()
        }

        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // Update current position periodically
    LaunchedEffect(mediaPlayer) {
        while (true) {
            delay(1000) // Update every second
            mediaPlayer?.let {
                currentPosition = it.currentPosition.toFloat()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = rememberAsyncImagePainter(model = track.imageUrl),
            contentDescription = "Track Image",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = track.title,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(
            text = track.artist,
            fontSize = 18.sp,
            color = Color.Gray
        )
        Slider(
            value = currentPosition,
            onValueChange = { newPosition ->
                currentPosition = newPosition
                mediaPlayer?.seekTo(newPosition.toInt())
            },
            valueRange = 0f..(mediaPlayer?.duration?.toFloat() ?: 1f)
        )
        Button(onClick = {
            if (isPlaying) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }
            isPlaying = !isPlaying
        }) {
            Text(if (isPlaying) "Pause" else "Play")
        }
    }
}
