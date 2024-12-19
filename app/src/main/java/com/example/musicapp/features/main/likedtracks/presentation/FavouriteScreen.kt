package com.example.musicapp.features.main.likedtracks.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp


import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.likedtracks.data.Track
import kotlinx.coroutines.delay

@Composable
fun FavouriteScreen(
    likedTracksViewModel: LikedTracksViewModel = hiltViewModel(),
    onTrackClick: (String) -> Unit
) {
    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()

    LaunchedEffect(Unit) {
        likedTracksViewModel.loadLikedTracks()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            likedTracksState.tracks.forEach { track ->
                TrackRow(
                    track = track,
                    isLiked = likedTracksState.likedTrackIds.contains(track.id),
                    onLikeClick = { likedTracksViewModel.toggleLike(track.id) },
                    onTrackClick = { onTrackClick(track.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                .size(64.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Gray
            )
            Text(
                text = track.artist,
                fontSize = 14.sp,
                color = Color.Gray
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
